Ext.QuickTips.init();
Ext.define('erp.controller.sys.alert.AlertList',{
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:['sys.alert.AlertList','core.button.PrintByCondition','core.button.AlertConfirm','core.button.Back','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.YnField',
        'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.PrintA4','core.button.Upload','core.button.ResAudit',
        'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
        'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.grid.YnColumn','core.button.Flow','core.button.Get',
        'core.button.GetMaterial','core.button.DeleteMaterial','core.button.ChangeMaterial','core.button.GetCraft','core.button.HistoryProdIO',
        'core.button.RefreshQty','core.button.MakeFlow','core.trigger.MultiDbfindTrigger','core.button.EnforceEnd',
        'core.button.CalMake','core.button.Check','core.button.ResCheck', 'core.button.End', 'core.button.ResEnd','core.button.ModifyMaterial',
        'core.button.SubRelation','core.button.TurnOSMake','core.button.GetOSVendor','core.button.UpdateRemark',
        'core.button.UpdateTeamcode','core.button.GetPrice','core.button.OSInfoUpdate','core.button.UpdateMaterialWH',
        'core.button.TurnOSToMake','core.button.BomUseMatch','erp.view.core.grid.HeaderFilter','core.button.UpdateMaStyle',
        'core.button.ShiPAddressUpdate','core.button.MrpOpen','core.button.MrpClose','core.button.DisableBomPast','core.button.CopyByConfigs'
        ],
	init:function(){
		var me = this;
		this.control({
			/*'erpFormPanel':{
				afterrender:function(v){
					var ad_cause = Ext.getCmp('ad_cause');
					var ad_solution = Ext.getCmp('ad_solution');
				}
			},*/
			'textareafield[name =ad_cause]':{
				afterrender:function(v){
					var ad_status = Ext.getCmp('ad_status').value;
					if(ad_status=='CLOSED'){
						v.setFieldStyle("background:rgb(224, 224, 224);color: black;");
						v.setReadOnly(true);
					}
				}
			},
			'textareafield[name =ad_solution]':{
				afterrender:function(v){
					var ad_status = Ext.getCmp('ad_status').value;
					if(ad_status=='CLOSED'){
						v.setFieldStyle("background:rgb(224, 224, 224);color: black;");
						v.setReadOnly(true);
					}
				}
			},
			'erpBackButton':{
				afterrender:function(btn){
					var ad_status = Ext.getCmp('ad_status');
					if(ad_status&&ad_status.value=='CLOSED'){
						btn.hide();
					}
				},
				click:function(btn){
					var ad_id = Ext.getCmp('ad_id').value;
					var ad_cause = Ext.getCmp('ad_cause').value;
					var ad_solution =Ext.getCmp('ad_solution').value;
					var form = Ext.getCmp('form');
					Ext.Ajax.request({
				   		url : basePath + 'sys/alert/revertAlertData.action',
				   		async: false,
				   		params: {
				   			caller: caller,
				   			id:ad_id,
				   			ad_cause:ad_cause,
				   			ad_solution:ad_solution
				   		},
				   		method : 'post',
				   		callback : function(options,success,response){
				   			var res = new Ext.decode(response.responseText);
				   			if(res.exceptionInfo){
				   				showError(res.exceptionInfo);
				   				return;
				   			}
							if(res.success){
								showMessage("提示", '回复成功！');
								window.location.href=window.location.href;
							}
				   		}
					});
				}
			},
			'erpCloseButton': {
				click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
 		   },
 		   'erpAlertConfirmButton':{
 			   afterrender:function(btn){
					var ad_status = Ext.getCmp('ad_status');
					if(ad_status&&ad_status.value=='CLOSED'){
						btn.hide();
					}
				},
 			   click:function(btn){
 				  Ext.MessageBox.confirm('提示', '确认后无法回复，是否确认?',function(btn){
				  		if(btn=='yes'){
							var ad_id = Ext.getCmp('ad_id').value;
							var ad_cause = Ext.getCmp('ad_cause').value;
							var ad_solution =Ext.getCmp('ad_solution').value;
							var form = Ext.getCmp('form');
							Ext.Ajax.request({
						   		url : basePath + 'sys/alert/confirmAlertData.action',
						   		async: false,
						   		params: {
						   			caller: caller,
						   			id:ad_id,
						   			ad_cause:ad_cause,
						   			ad_solution:ad_solution
						   		},
						   		method : 'post',
						   		callback : function(options,success,response){
						   			var res = new Ext.decode(response.responseText);
						   			console.log('确认成功！');
						   			if(res.exceptionInfo){
						   				showError(res.exceptionInfo);
						   				return;
						   			}
									if(res.success){
										showMessage("提示", '确认成功！');
										window.location.href=window.location.href;
									}
						   		}
							});
						}else{
	   						return;
	   					}
  			});
 			   }
 		   }
		});
	}
	
});