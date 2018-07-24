Ext.define('erp.view.hr.attendance.EmpWorkdateQuery', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	requires:['erp.view.hr.attendance.EwGridPanel'],
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				region : 'north',
				xtype : 'erpQueryFormPanel',
				anchor : '100% 15%',
				caller:'EmpWorkdateQuery',
				onQuery: function() {
					var grid = Ext.getCmp('querygrid');
					var ym=Ext.getCmp('q_date').value; 
					if(ym){
						var year=ym.substring(0,4); 
						var month=ym.substring(4,6); 
						var temp = new Date(year,month,0);
						var form = this;
						var condition = grid.defaultCondition || '';
						condition = form.spellCondition(condition);
						if(Ext.isEmpty(condition)) {
							condition = grid.emptyCondition || '1=1';
						}
						grid.setLoading(true);
						Ext.Ajax.request({
    						url: basePath + 'hr/attendance/getDatas.action',
	    					params:{condition: condition},
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
			    		var c=temp.getDate();
				        if(c==28){
				        	grid.columns[32].hide();
				        	grid.columns[33].hide();
				        	grid.columns[34].hide();				        	      	
				        }
						if(c==29){
							grid.columns[32].show();
							grid.columns[33].hide();
				        	grid.columns[34].hide();
				        }
						if(c==30){
							grid.columns[32].show();
							grid.columns[33].show();
				        	grid.columns[34].hide();
				        }
				        if(c==31){
				        	grid.columns[32].show();
							grid.columns[33].show();
				        	grid.columns[34].show();
				        }
			    			}
    					});
					}else{
						showError("请选择年月");
					}

			}
		}, {
				region : 'south',
				_noc : 1,
				xtype : 'erpEwGridPanel',
				anchor : '100% 85%',
				autoQuery : false
			} ]
		});
		me.callParent(arguments);
	}
});