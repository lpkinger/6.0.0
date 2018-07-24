Ext.define('erp.view.oa.flow.FlowFieldPanel', {
	extend: 'Ext.form.Panel', 
	alias : 'widget.FlowFieldPanel',
	layout: 'fit',
	autoScroll : true,
	BaseUtil:Ext.create('erp.util.BaseUtil'),
	GridUtil:Ext.create('erp.util.GridUtil'),
	initComponent : function() {
		var me = this;
		var items = [{
			xtype:'grid',
    		id:'fieldgrid' ,
    		title:'附件管理',
    		anchor:'100% 100%',
    		autoScroll:true,
    		tbar:[{
    			xtype: 'form',
				columnWidth: 1,
				frame: false,
				border: false,
				height: 36,
				bodyStyle: 'border:none;background:#e9e9e9;padding:2px;',
				layout: 'fit',
				items: [{
	    			cls:'x-btn-gray',
					xtype: 'filefield',
					name:'file',
					id:'flowfile',
					buttonText: '上传附件',
					buttonOnly: true,
					hideLabel: true,
					createFileInput : function() {
			            var me = this;
			            me.fileInputEl = me.button.el.createChild({
			            name: me.getName(),
			            tag: 'input',
			            type: 'file',
			            multiple:'multiple',
			            size: 1
			           }).on('change', me.onFileChange, me);
			        },
			        listeners: {
						change: function(field){
							if(field.value != null){
								field.ownerCt.ownerCt.ownerCt.ownerCt.upload(field.ownerCt, field);
							}
						}
					}
				}]
    		},'->',{
    			cls:'x-field-display',
    			xtype: 'displayfield',
        		value: '注:双击附件行下载，支持右键菜单',
        		margin:'0 15 0 0'
    		}],
    		columns:[{
    			xtype: 'rownumberer',
				text:'序号',
				width:40,
				align :'center'
    		},{
				dataIndex:'FF_NAME',
				flex:1,
				align:'left',
				text:'附件名称',
				style:'text-align:center;'
			},{
				dataIndex:'FF_SIZE',
				width:100,
				align:'center',
				text:'附件大小',
				renderer:function(value){
					if(value&&value!=null){
						return Ext.util.Format.fileSize(value);
					}
				}
			},{
				dataIndex:'FF_UPMAN',
				width:120,
				align:'center',
				text:'上传人'
			},{
				dataIndex:'FF_UPMANCODE',
				width:120,
				align:'center',
				text:'上传人编码'
			},{
				dataIndex:'FF_UPTIME',
				width:200,
				align:'center',
				text:'上传时间',
				xtype:'datecolumn',
				renderer:function(value){
					return Ext.Date.format(new Date(value),'Y-m-d H:i:s');
				}
			}],
			store:Ext.create('Ext.data.Store',{
			    storeId:'viewstore',
				fields : ['FF_ID','FF_FILEID','FF_UPMAN','FF_UPMANCODE','FF_UPTIME','FF_FDSHORTNAME','FF_KEYVALUE',
						  'FF_NAME','FF_CALLER','FF_SIZE'],
			    proxy: {
			        type: 'ajax',
			        url: basePath + '/oa/flow/getFile.action',
			        extraParams:{
						id : me._id
			        },
			        reader: {
			            type: 'json',
			            root: 'data'
			        }
			    },
			    autoLoad: true
			}),
			listeners:{
				itemdblclick : function(view, record){  
					SaveTwoButton('是否确定下载--'+record.data.FF_NAME+'？', function(btn){
						if(btn == 'yes'){
							view.ownerCt.ownerCt.download(record.data.FF_FILEID);
						}else{
							return;
						}
					});
				},
				itemcontextmenu : function(view, record, item, index, e){
					//阻止浏览器触发事件
					e.preventDefault();
					var contextmenu = new Ext.menu.Menu({ 
						items:[{
							iconCls:'x-button-icon-delete',
							text:'下载附件',
							handler:function(){
								view.ownerCt.ownerCt.download(record.data.FF_FILEID);
							}
						},{
							iconCls:'x-button-icon-close',
							text:'删除附件',
							handler:function(){
								SaveTwoButton('是否确定删除附件--'+record.data.FF_NAME+'？', function(btn){
									if(btn == 'yes'){
										formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
										var id = formCondition.split('=')[1];
										view.ownerCt.ownerCt.deleteFile(record.data.FF_FILEID,id,record.data.FF_NAME);
									}else{
										return;
									}
								});
							}
						}]
					});
					contextmenu.showAt(e.getXY());
				}
			}
		}]
		Ext.apply(me, { 
			items:items 
		}); 
		this.callParent(arguments);
	},
	upload: function(form, field){
		var me = this;
		//检查文件总量大小是否过量
		if(!me.checkUploadAmount(form)){
			return;
		}
		var files = form.getEl().down('input[type=file]').dom.files;
    	form.getForm().submit({
    		url:basePath + '/common/uploadFiles.action',
    		waitMsg: "正在上传",
    		method:'POST',
    		params:{
    			em_code:em_code,
    			caller:caller
    		},
    		success: function(form, action) {
    			if (action.result.noPower) {
    				Ext.MessageBox.alert("警告","对不起，您不是管理员或在该页面没有权限!");
    			}else{
    				formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
					var id = formCondition.split('=')[1];
    				//显示上传的文件
	    			Ext.MessageBox.alert("消息","恭喜,附件上传成功");
	    			//插入附件表
	    			var file = action.result.data;
	    			Ext.Ajax.request({
						url : basePath + 'oa/flow/saveFile.action',
						params:{
							file : Ext.JSON.encode(file),
							caller : caller,
							id : id,
							name : files[0].name
						},
						method : 'POST',
						async: false,
						callback : function(options,success,response){
							var rs = new Ext.decode(response.responseText);
							if(rs.exceptionInfo){
								showError(rs.exceptionInfo);return;
							}
							if(rs.success){
								Ext.getCmp('fieldgrid').store.load();
							}
						}
					});
    			}
		    }
    	});
	},
	checkUploadAmount:function(form){
		var files = form.getEl().down('input[type=file]').dom.files;
		var amounts = 0;
		for (var i = 0; i < files.length; i++) {
			amounts = amounts + files[i].size
		}
		if (amounts>104857600) {
			Ext.MessageBox.alert("警告","对不起,上传文件总大小超过100m");
			return false
		}
		return true;
	},
	download: function(id){	
		if (!Ext.fly('ext-attach-download')) {  
			var frm = document.createElement('form');  
			frm.id = 'ext-attach-download';  
			frm.name = id;  
			frm.className = 'x-hidden';
			document.body.appendChild(frm);
		}
		Ext.Ajax.request({
			url: basePath + 'common/downloadbyId.action',
			method: 'post',
			form: Ext.fly('ext-attach-download'),
			isUpload: true,
			params: {
				id: id
			}
		});
	},
	deleteFile: function(fileid,id,filename) {
		//更新附件表
		Ext.Ajax.request({
			url : basePath + 'oa/flow/updateFile.action',
			params:{
				fileid : fileid,
				id : id,
				filename:filename
			},
			method : 'POST',
			async: false,
			callback : function(options,success,response){
				var rs = new Ext.decode(response.responseText);
				if(rs.exceptionInfo){
					showError(rs.exceptionInfo);return;
				}
				if(rs.success){
					Ext.getCmp('fieldgrid').store.load();
	    			Ext.MessageBox.alert("消息","附件删除成功，可以在操作日志中还原附件");
				}
			}
		});
	}
});