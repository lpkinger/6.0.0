Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.CraftMake', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.mes.CraftMake','core.form.Panel','core.grid.Panel2',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.Update',
    		'core.form.YnField','core.grid.YnColumn', 'core.grid.TfColumn',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': {
    			reconfigure: function(grid){
    				Ext.defer(function(){
    					grid.readOnly = true;
    				}, 500);
    			}
    		},
    		'dbfindtrigger[name=ma_craftcode]' : {
    			afterrender: function(v) {
					var crid = Ext.getCmp('cr_id').value;
					if (crid != null & crid != '') {
						this.getGridStore('cr_id=' + crid);
					}
				},
				aftertrigger : function(v) {
					var crid = Ext.getCmp('cr_id').value;
					if (crid != null & crid != '') {
						this.getGridStore('cr_id=' + crid);
					}
				}
			},
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    				this.getGridStore('cr_id=' + Ext.getCmp('cr_id').value);
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
    				this.getGridStore('cr_id=' + Ext.getCmp('cr_id').value);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getGridStore : function(condition) {
		var me = this;
		var grid = Ext.getCmp('grid');
		grid.store.removeAll(false);
		me.BaseUtil.getActiveTab().setLoading(true);// loading...
		Ext.Ajax.request({// 拿到grid的columns
			url : basePath + "common/singleGridPanel.action",
			params : {
				caller : "CraftMake",
				condition : condition
			},
			method : 'post',
			callback : function(options, success, response) {
				me.BaseUtil.getActiveTab().setLoading(false);
				var res = new Ext.decode(response.responseText);
				if (res.exceptionInfo) {
					showError(res.exceptionInfo);
					return;
				}
				var data = [];
				if (!res.data || res.data.length == 2) {
					me.GridUtil.add10EmptyItems(grid);
				} else {
					data = Ext.decode(res.data.replace(/,}/g, '}').replace(
							/,]/g, ']'));
					if (data.length > 0) {
						grid.store.loadData(data);
					}
				}
			}
		});
	}
});