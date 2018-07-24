Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.RelatedParty', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	views : [
			'fa.gla.RelatedParty', 'core.grid.Panel2','core.button.Refresh','core.button.Close','core.trigger.MultiDbfindTrigger',
			'core.button.Update','core.button.DeleteDetail','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.grid.YnColumn'
			],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpUpdateButton' : {
				click : function(btn) {
					me.onUpdate();
				}
			},
			'erpRefreshButton' : {
				click : function(btn) {
					me.onRefresh();
				}
			},
    		'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			}
		})
	},
	onUpdate:function(){
		var me = this;
		var sets = me.GridUtil.getGridStore();
		if(sets.length<1){
			showError('未修改数据！');
		}
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'fa/gla/updateRelatedParty.action',
			params : {
				caller : caller,
				sets: '['+sets.toString()+']'
			},
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					updateSuccess(function(){
						window.location.reload();
					});
				} else if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
					return;
				}
			}
		});
	},
	onRefresh:function(){
		var me = this;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'fa/gla/refreshRelatedParty.action',
			params : {
				caller : caller
			},
			method : 'post',
			callback : function(options,success,response){
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.success){
					showMessage("提示","刷新关联方成功！");
				} else if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
					return;
				}
			}
		});
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	}
});