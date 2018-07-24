/**
 * 设置工作台的window
 */
Ext.define('erp.view.core.window.DeskTopSet', {
	extend: 'Ext.window.Window',
	alias: 'widget.desktopset',
	requires:['erp.view.common.DeskTop.ItemSelector'],
	id : 'win',
	title: '<font color=#CD6839>工作台设置</font>',
	iconCls: 'x-button-icon-set',
	height: screen.height*0.6,
	width: screen.width*0.4,
    maximizable : false,
	buttonAlign : 'center',
	layout : 'border',
	/**
	 * id:xtype,text 名称 ，count 默认条数 ，remove 可移除（true）
	 * @type 
	 */
	module: [{ id: 'flowportal',text: '审批流程(默认)',count:10,remove:false},
			 { id: 'commonuseportal',text: '常用模块(默认)',count:10,remove:false},
			 { id: 'taskportal',text: '待办任务(默认)',count:10,remove:false},
			 { id: 'infoportal',text: '消息(默认)',count:10,remove:false},
			 { id: 'inforemindportal',text: '消息提醒',count:10,remove:true},
			 { id: 'callportal',text: '客户生日提醒',count:10,remove:true},
			 { id: 'feedbackportal',text: '系统问题反馈',count:10,remove:true},
			 { id: 'kpibillportal',text: '考核管理',count:10,remove:true},
			 { id: 'subsportal',text: '我的订阅(默认)',count:10,remove:false},
			 { id: 'invitePortal',text: '邀请信息',count:10,remove:true},
			 { id: 'newflowportal',text: '业务流程',count:10,remove:true}],
	initComponent: function() {
		var me=this;
		this.getData();
		Ext.apply(me, { 
			items: [{
			region:'center',
			layout:'border',
			items:[{
					region:'center',
					xtype: 'itemselector',
					anchor: '100%',	
					id: 'itemselector-field',
					displayField: 'text',
					valueField: 'value',
					allowBlank: false,
					msgTarget: 'side'
			}],
			buttonAlign:'center',
			buttons:['->',{
				cls:'button1 pill',
				style:'margin-left:5px;',
				text:'确认',
				scope:this,
				handler:function(btn){
						var itemselector=Ext.getCmp('itemselector-field');
						var value=itemselector.getRawValue();
						if(value.length<1){
							showMessage('提示','选择需要设置对象',1000);
						}else {
							var data=new Array();
							var r=itemselector.toField.boundList.getStore().data.items;
							var detno=1;
							Ext.Array.each(r,function(item){
								data.push({
									xtype_:item.data.id,
									count_:item.data.count,
									detno_:detno
								});
								detno++;
							});
							var param=unescape(escape(Ext.JSON.encode(data)));
							me.setLoading(true);
							Ext.Ajax.request({
								url : basePath + '/common/desktop/setDeskTop.action',
								params : {param:param},
								method : 'post',
								callback : function(options,success,response){
									me.setLoading(false);
									var localJson = new Ext.decode(response.responseText);
									if(localJson.success){
										Ext.Msg.alert('提示','保存成功',function(){
											me.close();
											var contentWindow = Ext.getCmp("content-panel").items.items[0].body.dom.getElementsByTagName('iframe')[0].contentWindow;
        									contentWindow.location.reload();
											
										});
									} else{
										saveFailure();//@i18n/i18n.js
									}
								}
					
							});
						}
				}
			},{
				cls:'button1 pill',
				style:'margin-left:5px;',
				text:'关闭',
				handler:function(btn){
					btn.ownerCt.ownerCt.ownerCt.close();
				}
			},'->']
		}] 
		});
		this.callParent(arguments);
		this.show();
	},
	getData: function(){
		var me = this;
		Ext.Ajax.request({
			url : basePath + '/common/desktop/getBenchSet.action',
				callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.success){
					var to_=new Array(),from_=new Array(),m_=new Array();
					m_= Ext.Array.merge(me.module,res.module);
					from_=Ext.Array.clone(m_);
					var to_=new Array();
					for(var i=0;i<res.bench.length;i++){
						for(var j=0;j<m_.length;j++){
							if(res.bench[i].xtype_==m_[j].id){
								to_.push({
									id:m_[j].id,
									text:m_[j].text,
									count:res.bench[i].count_,
									remove:m_[j].remove
								});
								Ext.Array.remove(from_,m_[j]);
							}
						}
					}
					Ext.getCmp('itemselector-field').fromField.store.loadData(from_);
					Ext.getCmp('itemselector-field').toField.store.loadData(to_);
				} else if(res.exceptionInfo){
					showError(res.exceptionInfo);
				}
			}
		});
	}
});