Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.LabelPrintSetting', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['scm.reserve.labelPrintSetting.Viewport','scm.reserve.labelPrintSetting.labelPrintSettingGrid','core.toolbar.Toolbar', 'core.button.Save', 'core.button.Add',
			'core.button.Close', 'core.button.Update','core.button.DeleteDetail', 'core.button.Delete','core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger',
			'core.form.YnField'],
	init : function() {
		var me = this;
		this.control({
			'erpLabelPrintSettingGrid' : {
				select : function(selModel, record, index) {
					var grid = Ext.getCmp('grid');
					if (index == grid.store.data.items.length - 1) {// 如果选择了最后一行
						me.add10EmptyData(grid);
					}
					me.itemclick(grid);
				}
			},
			'erpSaveButton' : {
				click : function(btn) {
					var grid = Ext.getCmp('grid');
					var params = grid.getGridStore();
					if (params.length == 0) {
						showError("没有需要保存的数据！");
						return;
					}
					Ext.Ajax.request({
								url : basePath
										+ 'scm/reserve/saveLabelPrintSetting.action',
								params : {
									param : params,
									caller : caller
								},
								method : 'post',
								callback : function(options, success, response) {
									// me.getActiveTab().setLoading(false);
									var res = new Ext.decode(response.responseText);
									if (res && res.exceptionInfo) {
										showError(res.exceptionInfo);
										return;
									} else {
										gridCondition = (gridCondition == null)	? "":gridCondition.replace(/IS/g,"=");
										grid.GridUtil.loadNewStore(grid, {caller : caller,condition : gridCondition});
									}
								}
							});
				}
			},
			'text[name=lps_lasql]' : {
				focus : function() {
					var record = Ext.getCmp('grid').selModel.getLastSelected();
					var code = record.data['lps_lacode'];
					if (type == '' || type == null) {
						showError("请先选择标签编号 !");
					}
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpDeleteButton' : {
				click : function(btn) {
					var grid = Ext.getCmp('grid');
					var s = grid.getStore().data.items;									
					var bool = false;
					Ext.each(s,function (item,index){
						if(this.data['lps_id'] != ''&& this.data['lps_id'] != '0' && this.data['lps_id'] != 0){
							bool =  true;
						}
					});
					if(!bool){
						showError("没有需要删除的数据！");
						return;
					}
					warnMsg($I18N.common.msg.ask_del_main, function(btn){
					     if(btn == 'yes'){							
							Ext.Ajax.request({
								url : basePath+ 'scm/reserve/deleteLabelPrintSetting.action',
								params : {
									lps_caller : lps_caller,
									caller : caller
								},
								method : 'post',
								callback : function(options, success, response) {
								// me.getActiveTab().setLoading(false);
								var res = new Ext.decode(response.responseText);
									if (res && res.exceptionInfo) {
										showError(res.exceptionInfo);return;
									} else {
										delSuccess(function(){
											this.onClose();							
										});//
										gridCondition = (gridCondition == null)? "": gridCondition.replace(/IS/g,"=");
										grid.getGridColumnsAndStore({caller :caller,condition : gridCondition});}
									   }
								});
					         }
					});
				}
			}

		});
	},
	add10EmptyData : function(grid) {
		var data = new Array();
		var items = grid.store.data.items;
		for (var i = 0; i < 10; i++) {
			var o = new Object();
			o.lps_caller = lps_caller;
			o.lps_laid = null;
			o.lps_lacode = null;
			o.lps_id = null;
			o.lps_sql = null;
			data.push(o);
		}
		grid.store.insert(items.length, data);
	},
	itemclick : function(grid) {
		var btn = grid.down('erpDeleteDetailButton');
		if (btn)
			btn.setDisabled(false);
		btn = grid.down('erpAddDetailButton');
		if (btn)
			btn.setDisabled(false);
		btn = grid.down('copydetail');
		if (btn)
			btn.setDisabled(false);
		btn = grid.down('pastedetail');
		if (btn)
			btn.setDisabled(false);
		btn = grid.down('updetail');
		if (btn)
			btn.setDisabled(false);
		btn = grid.down('downdetail');
		if (btn)
			btn.setDisabled(false);
	}
});