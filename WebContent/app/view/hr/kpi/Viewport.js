Ext.define('erp.view.hr.kpi.Viewport',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	requires:['erp.view.hr.kpi.GridPanel'],
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'north',         
	    	  xtype:'erpQueryFormPanel',  
	    	  anchor: '100% 25%',
	    	  onQuery: function() {
				var grid = Ext.getCmp('querygrid');
				var form = this;
				var condition = grid.defaultCondition || '';
				condition = form.spellCondition(condition);
				if(Ext.isEmpty(condition)) {
					condition = grid.emptyCondition || '1=1';
				}
				form.beforeQuery(caller, condition);
				var gridParam = {caller: caller, condition: condition, start: 1, end: getUrlParam('_end')||1000};
				grid.setLoading(true);
				Ext.Ajax.request({
    				url: basePath + 'hr/kpi/kpiQuery.action',
    				params: {
    					condition: condition
    				},
    				callback: function(opt, s, r) {
    					grid.setLoading(false);
    					var res = Ext.decode(r.responseText);
    					var data = res.data;
        				if(!data || data.length == 0){
        					grid.store.removeAll();
        					grid.GridUtil.add10EmptyItems(grid);
        				} else {
    						grid.store.loadData(res.data);
    				}
    			}
    	});
			}
	    },{
	    	  region: 'south',         
	    	  xtype:'erpKpiQueryGridPanel', 
	    	  anchor: '100% 75%'
	    }]
		});
		me.callParent(arguments); 
	}
});