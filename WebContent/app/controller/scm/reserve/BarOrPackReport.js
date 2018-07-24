Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.BarOrPackReport', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'scm.reserve.BarOrPackReport', 'core.form.Panel','core.grid.Panel2', 'core.toolbar.Toolbar', 'core.grid.YnColumn',
			 'core.button.Add','core.button.Sync','core.button.Save', 'core.button.Close','core.button.Upload', 'core.button.Update',
			'core.button.Delete' ,'core.grid.detailAttach','core.trigger.DbfindTrigger','core.form.YnField', 'core.button.DeleteDetail',
			'core.button.Upload', 'core.form.FileField','core.trigger.MultiDbfindTrigger' ],
	init : function() {
		var me = this;
		this.control({
			'erpFormPanel' :{
				afterrender: function(f){
					var fo_caller = formCondition.split('=')[1].replace(new RegExp(/\'/g),'');
					if(fo_caller == 'ProdInOut!BarcodeIn'){
						f.setTitle('条码模板配置');
					}else if(fo_caller == 'ProdInOut!OutBoxcodeIn'){
						f.setTitle('外箱模板配置');
					}
				}
			},
			'erpGridPanel2' : {
				itemclick : this.onGridItemClick
			},
			'field[name=fo_caller]' : {
				beforerender:function(f){
					f.readOnly=true;
    				f.editable=false;
				},
				afterrender:function(f){
					f.setValue(getUrlParam('formCondition').split("=")[1].replace(/\'/g,""));
				}
  		  	},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					this.FormUtil.onUpdate(this);
				}
			},
			'erpDeleteButton' : {
				click : function(btn) {
					Ext.MessageBox.confirm("提示","<font color='red'>是否删除明细</font>",function(btn){
					 if(btn=='ok'||btn=='yes'){
						 Ext.Ajax.request({
	                         url : basePath + 'scm/reserve/deleteReportFile.action',
	                         params: {
	                             callers: Ext.getCmp('fo_caller').value,
	                             caller : caller
	                         },
	                         method : 'post',
	                         callback : function(options,success,response){
	                             var localJson = new Ext.decode(response.responseText);
	                             if(localJson.success){
	                                 Ext.Msg.alert("提示","删除成功!",function(){
	                                	 window.location.reload();
	                                 });
	                             } else {
	                            	 showError("删除失败");return;
	                             }
	                         }
	                     });
					 }else{
						 return;
					 }
					});
				}
			},
		});
	},
	onGridItemClick : function(selModel, record) {//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	}
});