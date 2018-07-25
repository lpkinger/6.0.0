Ext.QuickTips.init();
Ext.define('erp.controller.common.DealList', {
	extend : 'Ext.app.Controller',
	requires: ['erp.util.BaseUtil'],
	views : [ 'common.deallist.Viewport', 'common.datalist.GridPanel', 'common.batchDeal.Form', 'core.trigger.DbfindTrigger',
			'core.form.FtField', 'core.form.ConDateField', 'core.form.YnField', 'core.form.FtDateField','common.datalist.Toolbar',
			'core.form.MonthDateField','core.form.FtFindField', 'core.grid.YnColumn', 'core.grid.TfColumn', 
			'core.form.ConMonthDateField','core.button.Refresh' ],
	refs : [ {
		ref : 'grid',
		selector : '#grid'
	} ],
	init : function() {
		var me = this;
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.control({
			'erpBatchDealFormPanel button[id=query]' : {
				click : function(btn) {
					var grid = Ext.getCmp('grid');
					var form = btn.ownerCt.ownerCt, cond = form.getCondition(grid);
					grid.formCondition = cond;
					grid.getCount(null, null);
				}
			},
			'erpBatchDealFormPanel': {
				alladded : function(form) {
					var items = form.items.items;
					Ext.each(items, function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val)) {
							this.setValue(val);
							if(this.xtype == 'dbfindtrigger') {
								this.autoDbfind('form', caller, this.name, this.name + " like '%" + val + "%'");
							}
						}
					});
				},
				afterrender: function(f) {
					f.down('button[name=query]').handler = Ext.emptyFn;
					f.down('button[name=export]').handler = function(btn){
			    		var grid = Ext.getCmp('grid');
			    		var condition = f.getCondition(grid);
		    			if(Ext.isEmpty(condition)) {
		    				condition = '1=1';
		    			}
			    		grid.BaseUtil.createExcel(caller, 'datalist', condition, null, null, null, grid);
			    	};
				}
			},
			'erpVastDealButton': {
				click: function(btn) {
					if(caller == 'FeatureView!Query'){
						var ftcode = Ext.getCmp('ft_code').value;
		                if (Ext.isEmpty(ftcode)) {
		                	showError('模版编号不能为空！');
		                    return
		                } else {
		                	me.getGrid().setLoading(true);
		                    Ext.Ajax.request({
		                         url: basePath + 'pm/make/refreshFeatureView.action',
		                         method: 'post',
		                         params: {
		                        	 ftcode: ftcode
		                         },
		                         timeout: 1200000,
		                         callback: function(options, success, response) {
		                        	 me.getGrid().setLoading(false);
		                             var res = new Ext.decode(response.responseText);
		                             if (res.exceptionInfo != null) {
		                            	 showError(res.exceptionInfo);
		                                 return;
		                             }
		                             Ext.Msg.alert("提示", "刷新成功!", function() {
		                            	 var queryBtn = Ext.getCmp('query');
		                            	 queryBtn.fireEvent('click', queryBtn, queryBtn);
		                             });
		                        }
		                   });
		                }
					} else if(caller == 'FeatureView!Prod!Query'){
						var ftcode = Ext.getCmp('ft_code').value;
						if (Ext.isEmpty(ftcode)) {
		                	showError('模版编号不能为空！');
		                    return
		                } else {
		                	me.getGrid().setLoading(true);
		                    Ext.Ajax.request({
		                         url: basePath + 'pm/make/refreshFeatureViewProd.action',
		                         method: 'post',
		                         params: {
		                        	 ftcode: ftcode
		                         },
		                         timeout: 1200000,
		                         callback: function(options, success, response) {
		                        	 me.getGrid().setLoading(false);
		                             var res = new Ext.decode(response.responseText);
		                             if (res.exceptionInfo != null) {
		                            	 showError(res.exceptionInfo);
		                                 return;
		                             }
		                             Ext.Msg.alert("提示", "刷新成功!", function() {
		                            	 var queryBtn = Ext.getCmp('query');
		                            	 queryBtn.fireEvent('click', queryBtn, queryBtn);
		                             });
		                        }
		                   });
		                }
					} else {
						var currentMonth = btn.ownerCt.ownerCt.down('monthdatefield').value;
		                if (!currentMonth) {
		                	showError('期间不能为空!');
		                    return
		                } else {
		                	me.getGrid().setLoading(true);
		                    Ext.Ajax.request({
		                         url: basePath + 'scm/product/RefreshProdMonthNew.action',
		                         method: 'post',
		                         params: {
		                        	 currentMonth: currentMonth
		                         },
		                         timeout: 1200000,
		                         callback: function(options, success, response) {
		                        	 me.getGrid().setLoading(false);
		                             var res = new Ext.decode(response.responseText);
		                             if (res.exceptionInfo != null) {
		                            	 showError(res.exceptionInfo);
		                                 return;
		                             }
		                             Ext.Msg.alert("提示", "刷新成功!", function() {
		                            	 var queryBtn = Ext.getCmp('query');
		                            	 queryBtn.fireEvent('click', queryBtn, queryBtn);
		                             });
		                        }
		                   });
		                }
					}
	            }
	       },
			'monthdatefield': {
				afterrender: function(f) {
					var type = '', con = null;
					if(f.name == 'pwm_yearmonth' && (caller == 'Productwhmonth!subject' || caller == 'Productwhmonth!SubjectWarehouse' || caller=='Productwhmonth!warehouse')) {
						type = 'MONTH-P';
						con = Ext.getCmp('condatefield');
					}
					if(type != '') {
						this.getCurrentMonth(f, type, con);
					}
				}
			}
		});
	},
    getCurrentMonth: function(f, type, con) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: type
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				f.setValue(rs.data.PD_DETNO);
    				if(con != null) {
    					con.setMonthValue(rs.data.PD_DETNO);
    				}
    			}
    		}
    	});
    }
});