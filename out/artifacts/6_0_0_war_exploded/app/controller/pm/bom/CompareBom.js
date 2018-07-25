Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.CompareBom', {
	extend : 'Ext.app.Controller',
	views : [ 'pm.bom.CompareBom.Viewport', 'common.query.GridPanel', 'pm.bom.CompareBom.Form', 'core.trigger.DbfindTrigger',
			'core.form.FtField', 'core.form.ConDateField', 'core.form.YnField', 'core.form.FtDateField',
			'core.form.MonthDateField','core.form.FtFindField', 'core.grid.YnColumn', 'core.grid.TfColumn', 
			'core.form.ConMonthDateField' ],
	init : function() {
		this.control({
			'erpQueryFormPanel button[name=confirm]' : {
				click : function(btn) {

				}
			},
			'erpQueryGridPanel' : {
				itemclick : this.onGridItemClick
			},
			'erpQueryFormPanel1 button[name=query]': {
				click : function(btn) {
					var grid  = Ext.getCmp('querygrid');
					var form = Ext.getCmp('queryform');
					var values = '';
 					Ext.each(form.items.items, function(f){
						if(f.logic != null && f.logic != ''){
							if(f.value != null&&f.value != ''){
								values = values+f.value+",";
							}
						}
					});
 					values = values.substring(0,values.length-1);
					this.getGridColumnsAndStore(grid,values);
				}
			}
		});
	},
	onGridItemClick : function(selModel, record) {
	},
	getGridColumnsAndStore: function(grid,values){
		var me = this,grid=Ext.getCmp('querygrid');
		grid.setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "pm/bom/compareBom.action",
        	params: {
        		caller: caller, 
        		condition: values,
        		bd_single:Ext.getCmp('bd_single').checked,
        		bd_difbom:Ext.getCmp('bd_difbom').checked
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.data != null ? Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']')) : [];
        		if(!data || data.length == 0){
        			grid.store.removeAll();
        			me.add10EmptyItems(grid);
        		} else {
        			if(grid.buffered) {
        				var ln = data.length, records = [], i = 0;
        			    for (; i < ln; i++) {
        			        records.push(Ext.create(grid.store.model.getName(), data[i]));
        			    }
        			    grid.store.purgeRecords();
        			    grid.store.cacheRecords(records);
        			    grid.store.totalCount = ln;
        			    grid.store.guaranteedStart = -1;
        			    grid.store.guaranteedEnd = -1;
        			    var a = grid.store.pageSize - 1;
        			    a = a > ln - 1 ? ln - 1 : a;
        			    grid.store.guaranteeRange(0, a);
        			} else {
        				grid.store.loadData(data);
        			}
        		}
        		//自定义event
        		grid.addEvents({
        		    storeloaded: true
        		});
        		grid.fireEvent('storeloaded', grid, data);
        	}
        });
	},
	setStore: function(fields, data, groupField, necessaryField){
		if(!Ext.isChrome){
			Ext.each(fields, function(f){
				if(f.type == 'date'){
					f.dateFormat = 'Y-m-d H:i:s';
				}
			});
		}
		return Ext.create('Ext.data.Store', {
		    fields: fields,
		    data: data,
		    groupField: groupField,
		    getSum: function(records, field) {
		    	if (arguments.length  < 2) {
		    		return 0;
		    	}
	            var total = 0,
	                i = 0,
	                len = records.length;
	            if(necessaryField) {
            		for (; i < len; ++i) {//重写getSum,grid在合计时，只合计填写了必要信息的行
            			var necessary = records[i].get(necessaryField);
	            		if(necessary != null && necessary != ''){
		            		total += records[i].get(field);
		            	}
            		}
            	} else {
            		for (; i < len; ++i) {
            			total += records[i].get(field);
            		}
            	}
	            return total;
		    },
		    getCount: function() {
		    	if(necessaryField) {
		    		var count = 0;
		    		Ext.each(this.data.items, function(item){//重写getCount,grid在合计时，只合计填写了必要信息的行
			    		if(item.data[necessaryField] != null && item.data[necessaryField] != ''){
			    			count++;
			    		}
			    	});
		    		return count;
		    	}
		    	return this.data.items.length;
		    }
		});
	},
});