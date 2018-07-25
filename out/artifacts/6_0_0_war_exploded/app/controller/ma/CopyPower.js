Ext.QuickTips.init();
Ext.define('erp.controller.ma.CopyPower', {
	extend : 'Ext.app.Controller',
	views : [ 'ma.CopyPower', 'core.button.CopyPower', 'core.grid.Panel4', 'core.trigger.DbfindTrigger','core.toolbar.Toolbar3' ],
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	init : function() {
		var me = this;
		this.control({
			'button[id=copypower]': {// 复制权限
				click: function(btn) {
					me.CopyPower();
				}
			},
			'#search': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt;
		    		grid.GridUtil.loadNewStore(grid,{
		    			caller:caller,
		    			condition:'cp_haschange<>0 order by cp_id desc'
		    		});
    			}
    		},
    		'#close': {
    			click: function() {
    				this.BaseUtil.getActiveTab().close();
    			}
    		},
    		'erpGridPanel4':{
    			itemclick:this.GridUtil.onGridItemClick
    		}
		});
	},
	CopyPower : function() {
		var grid=Ext.getCmp('grid');
		var param=grid.GridUtil.getGridStore(grid);
		param = param == null ? [] : "[" + param.toString() + "]";
		grid.setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'ma/power/copypowerFromStandard.action',
			params: {
				param:unescape(param.toString())
			},
			callback : function(o, s, r) {
				grid.setLoading(false);
				if( s ) {
					var local = new Ext.decode(r.responseText);
					if(local.success){
						grid.GridUtil.loadNewStore(grid,{caller:caller,condition:'1=2'});
					}
				}
			}
		});
	}
});