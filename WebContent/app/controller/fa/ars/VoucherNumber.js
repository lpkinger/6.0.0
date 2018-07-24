Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.VoucherNumber', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'fa.ars.VoucherNumber', 'core.form.MonthDateField', 'common.editorColumn.GridPanel'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=query]': {
    			click: function(btn) {
    				var grid = btn.up('form').ownerCt.down('gridpanel');
    				me.query(grid);
    			}
    		},
    		'button[id=number]': {
    			click: function(btn) {
    				var grid = btn.up('form').ownerCt.down('gridpanel');
    				me.insertBreakNumber(grid);
    				Ext.getCmp('vo_breaks').setValue('');
    				grid.store.sort([{
    			        property : 'vo_lead',
    			        direction: 'ASC'
    			    },{
    			        property : 'vo_number',
    			        direction: 'ASC'
    			    },{
    			        property : 'vo_date',
    			        direction: 'ASC'
    			    }]);
    			}
    		},
    		'button[id=save]': {
    			click: function(btn) {
    				var grid = btn.up('form').ownerCt.down('gridpanel');
    				me.save(grid);
    			}
    		},
    		'monthdatefield': {
    			afterrender:function(f){
					me.getCurrentMonth(function(data){
						var ym = data ? data.PD_DETNO : Ext.Date.format(new Date(), 'Ym');
						f.setValue(ym);
					});  				
    			},
    			change: function(f) {
    				if(f.hasValid()) {
    					var grid = f.up('form').ownerCt.down('gridpanel');
    					me.query(grid);
    				}
    			}
    		},
    		
    		
    		
    		'erpEditorColumnGridPanel': {
    			storeloaded: function(grid, data) {
    				grid.difference = {};
    				if(data) {
    					var leads = [];
    					if(data instanceof Array) {
        					leads = Ext.Array.pluck(data, 'vo_lead');
        				} else if(data.data){
        					data.each(function(d){
        						leads.push(d.get('vo_lead'));
        					});
        				}
    					leads = Ext.Array.unique(leads), s = '';
    					Ext.Array.each(leads, function(l){
    						var breaks = me.getBreakNumber(grid, l);
    						grid.difference[l] = breaks;
    						if(breaks && breaks.length > 0) {
    							if(leads.length > 0 && l == '') {
        							s += ' (无): ' + breaks.join(',');
        						} else {
        							s += ' (' + l + '): ' + breaks.join(',');
        						}
    						}
    					});
    					Ext.getCmp('vo_breaks').setValue(s);
    				} else {
    					Ext.getCmp('vo_breaks').setValue(null);
    				}
    			}
    		}
    	});
    },
    getCurrentMonth: function(callback) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-A'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				callback.call(null, rs.data);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	query: function(grid){
		grid.GridUtil.loadNewStore(grid, {
			caller: caller,
			condition: 'vo_yearmonth=' + Ext.getCmp('vo_yearmonth').value + 
				' order by vo_lead,vo_number'
		});
	},
	getBreakNumber: function(grid, lead){
		var max = 0, nums = [], diff = [];
		grid.store.each(function(r){
			var l = r.get('vo_lead') || '', v = r.get('vo_number');
			if(l == lead) {
				max = Math.max(max, v);
				nums.push(Number(v));
			}
		});
		if(max > 0) {
			var numbers = [];
			for(var i = 1;i <= max;i++) {
				numbers.push(i);
			}
			return Ext.Array.difference(numbers, nums);
		}
		return null;
	},
	insertBreakNumber: function(grid){
		var items = grid.store.data.items, len = items.length;
		for(var i = len-1;i>=0;i--) {
			var item = items[i], breaks = grid.difference[item.get('vo_lead')];
			if(breaks) {
				if(breaks.length > 0) {
					if(breaks[0] < Number(item.get('vo_number'))) {
						item.set('vo_number', breaks[0]);
						Ext.Array.remove(breaks, breaks[0]);
					}
				} else
					delete grid.difference[item.get('vo_lead')]
			}
		}
	},
	save: function(grid){
		var dir = new Array(), numbers = [], lead, num, bool = true, i = 0;
		grid.store.each(function(item){
			i++;
			lead = item.get('vo_lead') || '(无)';
			num = item.get('vo_number');
			if(!Ext.Array.contains(numbers, lead + num)) {
				numbers.push(lead + num);
			} else {
				showError('有重号,位于第' + i + '行,凭证字:' + lead + ',凭证号:' + num);
				bool = false;return;
			}
			if(item.dirty) {
				dir.push({
					vo_id: item.get('vo_id'),
					vo_number: num
				});
			}
		});
		if(bool && dir.length > 0) {
			grid.setLoading(true);
			Ext.Ajax.request({
				url: basePath + 'fa/ars/insertBreakVoNumber.action',
				params: {
					data: Ext.encode(dir)
				},
				async: false,
				callback: function(opt, s, r) {
					grid.setLoading(false);
					var res = Ext.decode(r.responseText);
					if(res.success) {
						alert('保存成功');
					}
				}
			});
			this.query(grid);
		}
	}
});