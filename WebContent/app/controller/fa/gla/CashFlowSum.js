Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.CashFlowSum', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views: ['fa.gla.cashFlowSum.Viewport','fa.gla.cashFlowSum.CashFlowGrid',
     		'core.form.MonthDateField','core.button.Print', 'core.button.Close'],
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    init:function(){
    	var me = this;
    	this.control({
    		'erpCashFlowGrid': {
    			itemclick: function(selModel, record){
    				var treegrid = Ext.getCmp('cashflowgrid');
    				treegrid.selModel.select(record);
    				me.loadNode(selModel, record);
    			}
    		} ,
    		'erpVastPrintButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'monthdatefield': {
				afterrender: function(f) {
					this.getCurrentMonth(f, 'MONTH-A');
				}
			},
			'button[name=export]': {
				click: function(btn) {
					var grid = btn.ownerCt.ownerCt.ownerCt.down('cashflowgrid');
					this.BaseUtil.exportGrid(grid);
				}
			},
			'button[name=catchdata]': {
				click: function(btn) {
					this.onCatchData();
				}
			},
			'#setVoucher': {
				click: function(btn) {
					this.showSetVoucher();
				}
			},
			'#noSetVoucher': {
				click: function(btn) {
					this.showNoSetVoucher();
				}
			},
			'#cashflowset': {
				click: function(btn) {
					this.cashFlowSet();
				}
			}
    	});
    },
    loadNode: function(selModel, record){
    	var me = this;
    	var tree = Ext.getCmp('cashflowgrid');
    	if (!record.get('leaf')) { 
    		if(record.isExpanded() && record.childNodes.length > 0){//是根节点，且已展开
				record.collapse(true,true);//收拢
				me.flag = true;
				me.setParentNodes(selModel.ownerCt, record, false);
			} else {//未展开
				//看是否加载了其children
				if(record.childNodes.length == 0){
					//从后台加载
					tree.setLoading(true);
					Ext.Ajax.request({//拿到tree数据
			        	url : basePath + 'fa/gla/cashFlowSum.action',
			        	params: {
							yearmonth : Ext.getCmp('yearmonth').value,
							type : record.data.cfs_typename,
							catecode : record.data.cfs_catecode
						},
			        	callback: function(opt, s, r) {
							tree.setLoading(false);
							var rs = Ext.decode(r.responseText);
							if(rs.exceptionInfo) {
								showError(rs.exceptionInfo);
							} else {
								record.appendChild(rs.tree);
			        			record.expand(false, true);//展开
							}
			        	}
			        });
				} else {
					record.expand(false, true);//展开
				}
			}
    	}
    },
    getActiveTab: function(){
		var tab = null;
		if(Ext.getCmp("content-panel")){
			tab = Ext.getCmp("content-panel").getActiveTab();
		}
		if(!tab){
			var win = parent.Ext.ComponentQuery.query('window');
			if(win.length > 0){
				tab = win[win.length-1];
			}
		}
    	if(!tab && parent.Ext.getCmp("content-panel"))
    		tab = parent.Ext.getCmp("content-panel").getActiveTab();
    	if(!tab  && parent.parent.Ext.getCmp("content-panel"))
    		tab = parent.parent.Ext.getCmp("content-panel").getActiveTab();
    	return tab;
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
    },
    /**
	 * @param select 保留原筛选行
	 */
	onCatchData: function(){
		var yearmonth = Ext.getCmp('yearmonth').value;
		var grid = Ext.getCmp("cashflowgrid");
		var me = this;
		if(!Ext.isEmpty(yearmonth)){
			var activeTab = me.getActiveTab();
			activeTab.setLoading(true);
			Ext.Ajax.request({
				url: basePath + 'fa/gla/cashFlowSum.action',
				params: {
					yearmonth : yearmonth
				},
				async: false,
				callback: function(opt, s, r) {
					activeTab.setLoading(false);
					var rs = Ext.decode(r.responseText);
					if(rs.exceptionInfo) {
						showError(rs.exceptionInfo);
					} else {
						grid.store.setRootNode({
	                		text: 'root',
	                	    id: 'root',
	                		expanded: true,
	                		children: rs.tree
	                	});
					}
				}
			});
		}
	},
	cashFlowSet: function(){
		var grid = Ext.getCmp("cashflowgrid"), yearmonth = Ext.getCmp('yearmonth').value;
		var item = grid.view.contextMenu.record;
		if(item){
			var catecode = item.data.cfs_catecode;
			var catename = item.data.cfs_name;
			var cashcode = item.data.ca_defaultcashcode;
			var cashflow = item.data.ca_defaultcashflow;
			this.FormUtil.onAdd('CashFlowSet_'+catecode, '现金流量设置('+catecode+')', 'jsps/common/batchDeal.jsp?whoami=CashFlowSet&vo_yearmonth='+yearmonth+'&vd_catecode='+catecode+'&ca_name='+catename+'&flowcode='+cashcode+'&flowname='+cashflow+'');
		}
	},
	/**
	 * 已设置现金流量凭证
	 * @param btn
	 */
	showSetVoucher: function(btn){
		var yearmonth = Ext.getCmp('yearmonth').value;
		var grid = Ext.getCmp("cashflowgrid"), item = grid.view.contextMenu.record;
		if(yearmonth != null){
			if(item){
				var catecode = item.data.cfs_catecode;
				this.FormUtil.onAdd('ShowSetVoucher_'+yearmonth,'已设置现金流量凭证',"jsps/common/query.jsp?whoami=ShowSetVoucher&&vd_yearmonth="+yearmonth+'&vd_catecode='+catecode);
			}
		} else {
			showError("请选择期间");
		}
	},

	/**
	 * 未设置现金流量凭证
	 * @param btn
	 */
	showNoSetVoucher: function(btn){
		var yearmonth = Ext.getCmp('yearmonth').value;
		var grid = Ext.getCmp("cashflowgrid"), item = grid.view.contextMenu.record;
		if(yearmonth != null){
			if(item){
				var catecode = item.data.cfs_catecode;
				this.FormUtil.onAdd('ShowNoSetVoucher_'+yearmonth,'未设置现金流量凭证',"jsps/common/query.jsp?whoami=ShowNoSetVoucher&&vd_yearmonth="+yearmonth+'&vd_catecode='+catecode);
			}
		} else {
			showError("请选择期间");
		}
	}
});