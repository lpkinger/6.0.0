Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.SerialTrace', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.mes.SerialTrace','core.form.Panel','common.query.GridPanel',
    		'core.form.YnField','core.grid.YnColumn', 'core.grid.TfColumn',
    		'core.button.Query','core.button.Close',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpQueryButton' : {
    			click: function(btn) {
    				var sncode = Ext.getCmp('cm_sncode').value,
    					cond1, cond2;
    				if(!Ext.isEmpty(sncode)){
    					cond1 = 'cm_sncode=\'' + sncode + '\'';
            			cond2 = 'mb_sncode=\'' + sncode + '\'';
					}
    				var params1 = {
		   				caller: 'SerialTrace',
		   				condition : (cond1 || '1=1')
		   			};
    				//上料信息
        			var grid1 = Ext.getCmp('grid1');
        			me.GridUtil.loadNewStore(grid1, params1);
    				//不良记录
        			var grid2 = Ext.getCmp('grid2');
        			if(grid2) {
        				var params2 = {
        		   			caller: 'SerialTrace_2',
        		   			condition : (cond2 || '1=1')
        		   		};
        				me.GridUtil.loadNewStore(grid2, params2);
        			}
    			}
    		},
    		'#grid1' : {
    			afterrender: function(grid){
    				grid.store.on('datachanged', function(store){
						me.getBar(grid);
					});
    			}
    		},
			'tabpanel > #grid2-tab': {
				activate: function(panel) {
					if(panel.boxReady) {
						var grid2 = Ext.getCmp('grid2'), 
							sncode = Ext.getCmp('cm_sncode').value, 
							condition;
	        			if(grid2) {
	        				if(!Ext.isEmpty(sncode)){
	        					condition = 'mb_sncode=\'' + sncode + '\'';
	        				}
	        				var params = {
			   					caller: 'SerialTrace_2',
			   					condition : (condition || '1=1')
			   				};
	        				me.GridUtil.loadNewStore(grid2, params);
	        			}
					} else {
						panel.boxReady = true;
						var sncode = Ext.getCmp('cm_sncode').value, 
							condition;
						if(!Ext.isEmpty(sncode)){
        					condition = 'mb_sncode=\'' + sncode + '\'';
        				}
						panel.add({
							xtype: 'erpQueryGridPanel',
							caller: 'SerialTrace_2',
							anchor: '100% 100%',
							id : 'grid2',
							formCondition: condition
						});
					}
				}
			}
    	});
    },
    getBar: function(grid) {
    	var me = this, codes = [];
		grid.store.each(function(d){
			codes.push("'" + d.get('cm_barcode') + "'");
		});
		codes = Ext.Array.unique(codes);
		Ext.Ajax.request({
			url: basePath + 'scm/pm/mes/getBar.action',
			params: {
				codes: codes.join(',')
			},
			callback: function (opt, s, r) {
				if(s) {
					var rs = Ext.decode(r.responseText);
					if(rs.data) {
						Ext.Array.each(rs.data, function(d){
							d.BAR_INDATE = me.parseTimestamp(d.BAR_INDATE);
							d.BAR_VALIDDATE = me.parseTimestamp(d.BAR_VALIDDATE);
							d.BAR_MADEDATE = me.parseTimestamp(d.BAR_MADEDATE);
						});
						grid.barData = rs.data;
					}
				}
			}
		});
	},
	parseTimestamp: function(timeLong) {
		return timeLong == null ? null : Ext.Date.format(new Date(timeLong), 'Y-m-d');
	}
});