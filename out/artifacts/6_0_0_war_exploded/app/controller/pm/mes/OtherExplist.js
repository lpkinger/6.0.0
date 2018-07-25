Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.OtherExplist', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.mes.OtherExplist','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField', 
    		'core.button.Add','core.button.Save','core.button.Update','core.button.Close',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Delete','core.button.End','core.button.ResEnd','core.button.PrintByCondition',
    		'core.form.YnField','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.UpdateOtherExplistInfo'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			reconfigure:function(){
    				   var status = Ext.getCmp('ma_statuscode');
	 				   if(status &&  status.value == 'COMMITED'){
	 					  var grid=Ext.getCmp('grid');
		 					Ext.each(grid.columns,function(c){
		 						if(c.dataIndex=='md_price' || c.dataIndex=='md_taxrate' ){
		 							c.autoEdit=true;
		 						}	 					
		 					});
	 				   }
    				  
    			},
    			itemclick: function(selModel, record) {
    				if (record.data.md_id != 0 && record.data.md_id != null && record.data.md_id != '') {
        				var btn = Ext.getCmp('erpProcessingDetButton');
        				btn && btn.setDisabled(false);
        			}
    				me.onGridItemClick(selModel, record);
    			}
			},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				this.FormUtil.beforeSave(this);
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
    				me.FormUtil.onDelete(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addOtherExplist', '新增加工委外单', 'jsps/pm/mes/otherExplist.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpEndButton': {
 			   afterrender: function(btn){
 				   var status = Ext.getCmp('ma_statuscode');
 				   if(status && status.value != 'AUDITED'){
 					   btn.hide();
 				   }
 			   },
 			   click: function(btn){
 				   me.FormUtil.onEnd(Ext.getCmp('ma_id').value);
 			   }
 		   },
 		   'erpResEndButton': {
 			   afterrender: function(btn){
 				   var status = Ext.getCmp('ma_statuscode');
 				   if(status && status.value != 'FINISH'){
 					   btn.hide();
 				   }
 			   },
 			   click: function(btn){
 				   me.FormUtil.onResEnd(Ext.getCmp('ma_id').value);
 			   }
 		   },
 		   '#erpProcessingDetButton': {
		    	//加工内容明细
			    click: function(btn) {
		        	var grid = Ext.getCmp('grid'), record = grid.getSelectionModel().getLastSelected();
			    	var md_id = record.get('md_id');
			    	if(md_id){
			    		me.loadDetail(md_id);
			    	}
			    }
			},
	 		'#ma_vendcode':{
	 			beforerender: function(f){
	 				var status = Ext.getCmp('ma_statuscode');
    				if(status.value == 'COMMITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				} 
	 			}
	 		},
	 		'#ma_currency':{
	 			beforerender: function(f){
	 				var status = Ext.getCmp('ma_statuscode');
    				if(status.value == 'COMMITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				} 
	 			}
	 		},
			'erpUpdateOtherExplistInfoButton':{
				 afterrender: function(btn){
	 				   var status = Ext.getCmp('ma_statuscode');
	 				   if(status &&  status.value != 'COMMITED'){
	 					   btn.hide();
	 				   }		 				
	 			   },
	 			   click: function(btn){
	 				   me.updateOtherExplistInfo();
	 			   }
			}
    	});
    },
    loadDetail: function(md_id) {
		 var me=this;
    	var width = Ext.isIE ? screen.width*0.7*0.9 : '80%', height = Ext.isIE ? screen.height*0.75 : '80%';
    	Ext.create('Ext.Window', {
    		width: width,
    		height: height,
    		autoShow: true,
    		layout: 'anchor',
    		items: [{
    			tag : 'iframe',
    			frame : true,
    			anchor : '100% 100%',
    			layout : 'fit',
    			html : '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/pm/mes/otherExplistDetail.jsp?formCondition=md_id=' 
    				+ md_id + '&gridCondition=oed_mdid=' + md_id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
    		}],
    		listeners: {
    		      beforeClose: function (sender, handlers) {
     			     window.location.reload();
    		      }
    	    } 
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	updateOtherExplistInfo:function(){
		var me=this;
		var maid=Ext.getCmp('ma_id').value;
		var vecode=Ext.getCmp('ma_vendcode');
		var currency=Ext.getCmp('ma_currency');
		var param = me.GridUtil.getGridStore();
		param = param == null ? [] : "[" + param.toString() + "]";
		if(vecode && !Ext.isEmpty(vecode.value) && !Ext.isEmpty(vecode.value)){
			Ext.Ajax.request({
				url : basePath + "pm/mes/updateOtherExplistInfo.action",
				params: {
					caller	:caller, 
					id		:maid,
					vecode  :vecode.value,
				    currency:currency.value,
				    param   :param
				},
				method : 'post',
				async: false,
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exceptionInfo){
						showError(res.exceptionInfo);
						return;
					}
					if(res.success){
						showMessage('提示', '保存成功!', 1000);
						window.location.reload();
					}
				}
			});
		}else{
			showError('委外商及币别不能为空!');
			return false;
		}
	}
});