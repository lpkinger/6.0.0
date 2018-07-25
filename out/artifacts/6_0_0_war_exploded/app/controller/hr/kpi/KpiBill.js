Ext.QuickTips.init();
Ext.define('erp.controller.hr.kpi.KpiBill', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.kpi.KpiBill','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.grid.YnColumn','core.button.Scan','core.button.KpiSaveComment',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    		'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit','core.trigger.TextAreaTrigger',
    		'core.trigger.DbfindTrigger','core.form.YnField','core.button.DeleteDetail','core.button.Upload','core.form.FileField',
    		'core.trigger.MultiDbfindTrigger','core.trigger.SchedulerTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			beforeedit:function(o){
    				 var isedit = o.record.get('ki_isedit');
    	               if(isedit == 0)
    	                   return false;  
    			},
    			itemclick: function(view,record){
    				if(Ext.getCmp('erpKPiSaveComment')!=null)
    		    		Ext.getCmp('erpKPiSaveComment').setDisabled(false);
    			},
    			itemdblclick: function(view,record){
    			  var value=record.data['ki_criterion'];
    			  var height=400,width=600;
    			  var col=Ext.getCmp('grid').columns;
    			  var h_w='';
    			  Ext.each(col,function(c){
    			   if(c.dataIndex=='ki_criterion'){
    			   		h_w=c.renderName;
    			   }
    			  });
    			  var arr=h_w.split('#');
    			  if(arr.length==2){
    			  	height=arr[0];
    			  	width=arr[1];
    			  }
    			  var win =new Ext.window.Window({
						id: 'criterion-win',
						title: '评分细则',
						height: height,
						width: width,
						layout:'fit',
						resizable:true,								    
					    buttonAlign: 'center',
						items:[{
						   	xtype:'form',
						    baseCls : "x-plain",
						    layout:'fit',
						    items: [{
							    	xtype: 'textarea',
							    	fieldLabel: '',
							    	value: value
							}]	
						}],
						buttons: [{
							    	text:'关闭',
							    	cls: 'x-btn-gray',
							    	handler: function(btn) {
							    	win.close();
							    	}
						}]	
				});
				win.show();					
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('kb_id').value));
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('kb_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('kb_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('kb_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('kb_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('kb_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('kb_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('kb_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},     			
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('kb_id').value);
    			}
    		},
    		'erpKPiSaveComment':{
    			afterrender: function(btn){
					btn.setDisabled(true);
    			},
				click: function(btn){
    				var comment=btn.ownerCt.ownerCt.ownerCt.items.items[1].selModel.selected.items[0].data['kbd_comment'];
    				if(comment!=''){
    					var params = new Object();
    					var kc_id,kc_emid,kc_context;
    					Ext.Ajax.request({
    						url : basePath +'common/getId.action?seq=KpiComment_SEQ',
    						method : 'get',
    						async: false,
    						callback : function(options,success,response){
    							var rs = new Ext.decode(response.responseText);
    							if(rs.exceptionInfo){
    								showError(rs.exceptionInfo);return;
    							}
    							if(rs.success){
    								kc_id=rs.id;
    								kc_emid=Ext.getCmp('kb_manid').value;
    								kc_context=comment;
    							};
    						}
    					});
    					params.formStore =unescape(escape(Ext.JSON.encode({"kc_id":kc_id,"kc_emid":kc_emid,"kc_context":kc_context})));
    					params.param =[];
    					Ext.Ajax.request({
    						url : basePath +'common/saveCommon.action?caller=KpiComment',
    						params : params,
    						method : 'post',
    						callback : function(options,success,response){
    							var localJson = new Ext.decode(response.responseText);
    							if(localJson.success){
    							Ext.Msg.alert('提示', '收藏成功');
    							}else if(localJson.exceptionInfo){
    								var str = localJson.exceptionInfo;
    								showError(str);
    								return;
    							} else {
    								saveFailure();
    							}
    						}
    					});
    				}
    			}		
    		},
    		'dbfindtrigger[name=kbd_comment]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='kb_manid';
	    			trigger.mappingKey='kc_emid';
    			}
    		}
    	});
    },
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});