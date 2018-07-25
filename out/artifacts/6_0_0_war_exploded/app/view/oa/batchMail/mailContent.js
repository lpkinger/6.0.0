Ext.define('erp.view.oa.batchMail.mailContent', {
	extend: 'Ext.panel.Panel',
	layout: 'border',
	alias: 'widget.erpMailContentPanel',
	autoScroll: true,
	enableKeyEvents: true,
	FormUtil: Ext.create('erp.util.FormUtil'),
	items: [{
		xtype: 'form',
		id: 'form',
		title: '<font style="color:black;height:20px;line-height:20px;font-size:16px;font-weight:600">邮件发送</font>',
		bodyStyle:'background-color:#f1f1f1 !important;',
		region: 'center',
		layout: 'anchor',
		autoScroll: true,
		buttonAlign:'center',
		items: [{
			xtype: 'checkboxgroup',
	        fieldLabel: '<font style="color: red;">搜索范围</font>',
	        id: 'searchType',
	        // Arrange radio buttons into two columns, distributed vertically
	        //columns: 2,
	        vertical: false,
	        width: 300,
	        items: [
	            { boxLabel: '客户', name: 'type', inputValue: '1', checked: true},
	            { boxLabel: '供应商', name: 'type', inputValue: '2'}
	        ]
		},{
			id: 'reciveman',
			bodyStyle:'background-color:#f1f1f1 !important;',
			padding: '5px 0px',
			data:{spanEl:[],inputEl:{id:'must'}},
			tpl: '<div style="display:flex"><label><span class="mySpan">收件人地址<span style="color:black">:</span></span></label>'
				+ '<div id="main" style="right:100px;border:1px solid #8b8970;flex: 1;">'
				+ '<tpl for="spanEl"><span style="line-height:22px;float:left;cursor:pointer;">{text}</span></tpl><input id="must" width="300px"></input>'
				+ '</div><button id="choseButton"></button></div>'
		},{
			id: 'rec_dbfind',
			xtype: 'multidbfindtrigger',
			name: 'rec_dbfind',
			hidden: true
		},{
			xtype: 'textfield',
			fieldLabel: '<font style="color: red;">邮件主题</font>',
			allowBlank: false,
			id: 'emailtheme',
			anchor: '100%'
		},{
			xtype: 'htmleditor',
			fieldLabel: '<font style="color: red;">邮件内容</font>',
			allowBlank: false,
			anchor: '100% 70%',
			id: 'emailcontent',
		},{
			xtype: 'mfilefield',
			id: 'attachFiles'
		}],
		buttons: [{
			text: '发送',
			formBind: true,
			handler: function(btn){
				var array = [],
					main = document.getElementById('main'),
					childNodes = main.childNodes,
					len = childNodes.length;
				if(len > 1){
					//获取收件人
					for(var i = 0; i < len - 1; i++){
						if(childNodes[i].style.color != 'red')
							array.push(childNodes[i].textContent)
					}
					if(array.length == 0){
						Ext.Msg.alert('提示','请至少选择一个有效的收件人!');
						return;
					}
					var theme = Ext.getCmp('emailtheme').getValue(),
						content = Ext.getCmp('emailcontent').getValue(),
						attachs = Ext.getCmp('attachFiles').items.items,
						fileArray = [];
					var recivemen = array.join('');
					recivemen = recivemen.substring(0,recivemen.length-1);
					if(attachs.length > 2){
						Ext.Array.each(attachs, function(attach){
							if(attach.realpath){
								var obj = {
									filename: attach.fileName,
									filepath: attach.realpath
								};
								fileArray.push(obj);
							}
						});
					}
					Ext.getCmp('view_batchMail').getEl().mask('loading');
					Ext.Ajax.timeout = 90000;
					Ext.Ajax.request({
						url: basePath + 'oa/batchMail/send.action',
						params: {
							recivemen: recivemen,
							subject: theme,
							content: content,
							files: JSON.stringify(fileArray)
						},
						callback: function(options, success, response){
							Ext.getCmp('view_batchMail').getEl().unmask();
							if(response.timeout){
								Ext.Msg.alert('提示','选择的文件过大!');
							}
							var res = Ext.decode(response.responseText);
							if(res && res.success){
								Ext.Msg.alert('提示','邮件正在发送,您可前往邮件状态列表查看发送状态!',function(){
									//重新加载tabpanel，清空内容
									document.location.reload();
								});
							}
						}
					});
				}else{
					Ext.Msg.alert('提示','请至少输入一个收件人!');
				}
			}
		},{
			text: '关闭',
			handler: function(btn){
				var p = parent.Ext.getCmp('content-panel');
				p.getActiveTab().close();
			}
		}]
	}],
	initComponent: function(){
		var me = this;
		if(this.enableKeyEvents) {
			this.addKeyBoardEvents();
		}
		me.callParent(arguments);
		
	},
	addKeyBoardEvents: function(){
		var me = this;
		Ext.EventManager.addListener(document.body, 'keydown', function(e){
			if(e.altKey && e.ctrlKey && e.keyCode == Ext.EventObject.P) {
				me.FormUtil.onAdd('configs-' + caller, '逻辑配置维护(' + caller + ')', "jsps/ma/logic/config.jsp?whoami=" + caller);
			}
		});
	}
});