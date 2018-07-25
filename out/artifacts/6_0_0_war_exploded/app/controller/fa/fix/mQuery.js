Ext.QuickTips.init();
Ext.define('erp.controller.fa.fix.mQuery', {
    extend: 'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'fa.fix.mQuery.Viewport','fa.fix.mQuery.GridPanel','fa.fix.mQuery.QueryForm','fa.fix.mQuery.QueryWin',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.ConDateField','core.form.YnField',
     		'core.form.FtDateField','core.form.FtFindField','core.grid.YnColumn','core.grid.TfColumn','core.form.ConMonthDateField'
     	],
    init:function(){
    	this.control({
    		'erpQueryFormPanel button[name=confirm]': {
    			
    		},
    		'button[name=export]': {
				click: function(btn) {
					var grid = btn.ownerCt.ownerCt.ownerCt.down('#mquerygrid');
					this.BaseUtil.exportGrid(grid);
				}
			},
    		'button[name=query]':{
    			afterrender: function(btn){
    				var me = this;
    				var filter = me.createFilterPanel(btn);
    				filter.show();
    			},
    			click: function(btn){
    				var me = this;
    		    	if(Ext.getCmp(btn.getId() + '-filter')){
    		    		Ext.getCmp(btn.getId() + '-filter').show();
    		    	}else{
    		    		var filter = me.createFilterPanel(btn);
    		    		filter.show();
    		    	}
    			}
    		}
    	});
    },
    
    createFilterPanel:function(btn){
    	
    	var me = this;
    	
    	var filter = Ext.create('Ext.Window', {
    		id: btn.getId() + '-filter',
    		style: 'background:#f1f1f1',
    		title: '筛选条件',
    		width: 500,
    		height: 385,
    	    layout: 'column',
    	    defaults: {
    	    	margin: '2 2 2 10'
    	    },
    	    items: [{
				id: 'cm_yearmonth',
				name: 'cm_yearmonth',
				xtype: 'conmonthdatefield',
				fieldLabel: '期间',
				labelWidth: 80,
				margin: '10 2 2 10',
				columnWidth: .51,
				getValue: function() {
					if(!Ext.isEmpty(this.value)) {
						return {begin: this.firstVal, end: this.secondVal};
					}
					return null;
				},
				listeners: {
					afterrender: function(f) {
						me.getCurrentMonth(f);
					}
				}
			}],
			buttonAlign: 'center',
    	    buttons: [{
	    		text: '确定',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		handler: function(btn) {
    				var fl = btn.ownerCt.ownerCt;
					var	con = me.getCondition(fl);
    				
					me.query(con);
					fl.hide();
	    		}
	    	},{
	    		text: '关闭',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		handler: function(btn) {
	    			
	    			var fl = btn.ownerCt.ownerCt;
	    			fl.hide();
	    		}
	    	}]
    	});
		return filter;
    
    },
    getCondition: function(pl) {
    	var r = new Object(),v;
    	Ext.each(pl.items.items, function(item){
    		if(item.getValue !== undefined) {
    			v = item.getValue();
        		if(!Ext.isEmpty(v)) {
        			r[item.id] = v;
        		}
    		}
    	});
    	var tb = Ext.getCmp('gl_info_ym');
    	if(tb)
    		tb.updateInfo(r);
    	return r;
    },
    query: function(cond) {
    	var grid = Ext.getCmp('mquerygrid');
    	Ext.Ajax.request({
    		url: basePath + 'fa/fix/mQueryController/getMQuery.action',
    		params: {
    			condition: Ext.encode(cond)
    		},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(grid && res.data) {
        			var data = new Array();
        			Ext.Array.each(res.data, function() {
        				var keys = Ext.Object.getKeys(this);
        				var obj = new Object();
        				for(var i in keys) {
        					obj[keys[i].toLowerCase()] = this[keys[i]];
        				}
        				data.push(obj);
        			});
        			grid.store.loadData(data);
        		}
        		grid.setLoading(false);
        	}
    	});
    },
    getCurrentMonth: function(f) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-F'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				f.setValue(rs.data.PD_DETNO);
    			}
    		}
    	});
    }
});