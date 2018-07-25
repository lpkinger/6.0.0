Ext.define('erp.view.opensys.billPlanTrace.ProdInOutMakeInGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.ProdInOutMakeInGrid',
	region: 'south',
	layout : 'fit',
	id: 'makeingrid',
	cls: 'custom',
 	emptyText : $I18N.common.grid.emptyText,
    useArrows: true,
    rootVisible: false,
    GridUtil: Ext.create('erp.util.GridUtil'),
	bodyStyle:'background-color:#f1f1f1;',
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	constructor: function(cfg) {
		if(cfg) {
			cfg.plugins = cfg.plugins || [ Ext.create('erp.view.core.plugin.CopyPasteMenu')];
	    	Ext.apply(this, cfg);
		}
		this.callParent(arguments);
	},
	initComponent : function(){ 
		Ext.override(Ext.data.AbstractStore,{
			indexOf: Ext.emptyFn
		});
		var sa_code = Ext.getCmp('sa_code').value;
		var sd_detno = Ext.getCmp('sa_sourceid').value;
		var type = '完工入库单';
		if(sa_code&&sd_detno){
			condition = 'pd_piclass = \''+type+'\' and sa_code = \''+sa_code+'\' and sd_detno = \''+sd_detno+'\'';
		}else{
			condition = '1=2';
		}
		this.defaultCondition = condition;
		var gridParam = {caller: 'ProdInOutMakeIn', condition: condition};
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");
		this.callParent(arguments);
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	emptyCondition: '1=2',
	columns: new Array()
});