Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketmgr.annualPlans.YeardisingQuery', {
	extend : 'Ext.app.Controller',
	views : [ 'common.query.Viewport', 'common.query.GridPanel', 'common.query.Form', 'core.trigger.DbfindTrigger',
			'core.form.FtField', 'core.form.ConDateField', 'core.form.YnField', 'core.form.FtDateField','core.form.YearDateField',
			'core.form.MonthDateField','core.form.FtFindField', 'core.grid.YnColumn', 'core.grid.TfColumn', 
			'core.form.ConMonthDateField', 'core.trigger.TextAreaTrigger' ],
	init : function() {
		var  me=this;
		this.control({
			'erpQueryFormPanel' : {
				alladded : function(form) {
					var items = form.items.items, autoQuery = false;
					Ext.each(items, function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val)) {
							this.setValue(val);
							autoQuery = true;
							if(this.xtype == 'dbfindtrigger') {
								this.autoDbfind('form', caller, this.name, this.name + " like '%" + val + "%'");
							}
						}
					});
					if(autoQuery) {
						setTimeout(function(){
							form.onQuery();
						}, 1000);
					}
				}
			},
			'erpQueryGridPanel' : {
				itemclick : this.onGridItemClick,
				storeloaded: function (grid){
					var data=grid.store.prefetchData;
					var o=new Object();
					for(var i=1;i<13;i++){
						o['mhd_total'+i]=0;
						o['mhd_qty'+i]=0;
					}
					o['mhd_sumtotal']=0;
					o['mhd_sumqty']=0;
					o['mhd_prodcode']='合计';	
					Ext.Array.each(data.items,function(item,index){
						if(item.data.mhd_id){				
							o['mhd_total1']+=Number(item.data['mhd_total1']);
							o['mhd_total2']+=Number(item.data['mhd_total2']);
							o['mhd_total3']+=Number(item.data['mhd_total3']);
							o['mhd_total4']+=Number(item.data['mhd_total4']);
							o['mhd_total5']+=Number(item.data['mhd_total5']);
							o['mhd_total6']+=Number(item.data['mhd_total6']);
							o['mhd_total7']+=Number(item.data['mhd_total7']);
							o['mhd_total8']+=Number(item.data['mhd_total8']);
							o['mhd_total9']+=Number(item.data['mhd_total9']);
							o['mhd_total10']+=Number(item.data['mhd_total10']);
							o['mhd_total11']+=Number(item.data['mhd_total11']);
							o['mhd_total12']+=Number(item.data['mhd_total12']);
							o['mhd_qty1']+=Number(item.data['mhd_qty1']);
							o['mhd_qty2']+=Number(item.data['mhd_qty2']);
							o['mhd_qty3']+=Number(item.data['mhd_qty3']);
							o['mhd_qty4']+=Number(item.data['mhd_qty4']);
							o['mhd_qty5']+=Number(item.data['mhd_qty5']);
							o['mhd_qty6']+=Number(item.data['mhd_qty6']);
							o['mhd_qty7']+=Number(item.data['mhd_qty7']);
							o['mhd_qty8']+=Number(item.data['mhd_qty8']);
							o['mhd_qty9']+=Number(item.data['mhd_qty9']);
							o['mhd_qty10']+=Number(item.data['mhd_qty10']);
							o['mhd_qty11']+=Number(item.data['mhd_qty11']);
							o['mhd_qty12']+=Number(item.data['mhd_qty12']);
							o['mhd_sumqty']+=Number(item.data['mhd_sumqty']);
							o['mhd_sumtotal']+=Number(item.data['mhd_sumtotal']);
						}
					});
					grid.store.insert(0,o);				
					grid.getView().addRowCls(0,'custom-alt');
				}
			},
			'monthdatefield': {
				afterrender: function(f) {
					var type = '';
					if(f.name == 'cd_yearmonth') {
						type = 'MONTH-T';
					}
					if(f.name == 'cmc_yearmonth') {
						type = 'MONTH-A';
					}
					if(f.name == 'cm_yearmonth') {
						type = 'MONTH-A';
					}
					if(type != '' && Ext.isEmpty(getUrlParam(f.name))) {
						this.getCurrentMonth(f, type);
					}
				}
			}
		});
	},
	onGridItemClick : function(selModel, record) {
		if (caller == 'CustMonth!ARLI!Query') {
			var cmid = record.data['cm_id'];
			if (cmid > 0) {
				var panel = Ext.getCmp(caller + "cm_id" + "=" + cmid);
				var main = parent.Ext.getCmp("content-panel");
				if (!main) {
					main = parent.parent.Ext.getCmp("content-panel");
				}
				if (!panel) {
					var title = "";
					if (value.toString().length > 4) {
						title = value.toString().substring(value.toString().length - 4);
					} else {
						title = value;
					}
					var myurl = '';
					if (me.BaseUtil.contains(url, '?', true)) {
						myurl = url + '&formCondition=' + formCondition + '&gridCondition=' + gridCondition;
					} else {
						myurl = url + '?formCondition=' + formCondition + '&gridCondition=' + gridCondition;
					}
					myurl += "&datalistId=" + main.getActiveTab().id;
					main.getActiveTab().currentStore = me.getCurrentStore(value);// 用于单据翻页
					panel = {
						title : me.BaseUtil.getActiveTab().title + '(' + title + ')',
						tag : 'iframe',
						tabConfig : {
							tooltip : me.BaseUtil.getActiveTab().tabConfig.tooltip + '(' + keyField + "=" + value + ')'
						},
						frame : true,
						border : false,
						layout : 'fit',
						iconCls : 'x-tree-icon-tab-tab1',
						html : '<iframe id="iframe_maindetail_' + caller + "_" + value + '" src="' + myurl
								+ '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
						closable : true,
						listeners : {
							close : function() {
								if (!main) {
									main = parent.parent.Ext.getCmp("content-panel");
								}
								main.setActiveTab(main.getActiveTab().id);
							}
						}
					};
					this.openTab(panel, caller + keyField + "=" + record.data[keyField]);
				} else {
					main.setActiveTab(panel);
				}

			}
		}
		if(caller=='Sale!saledetailDet'){
			this.schedule(record);
		}
	},
	schedule: function(record) {
		var width = Ext.isIE ? screen.width*0.7*0.9 : '80%',
	   		height = Ext.isIE ? screen.height*0.75 : '100%';
		var sd_id = record.get('sd_id');
		Ext.Ajax.request({
			url : basePath + "scm/sale/checkSaleDetailDet.action",
			params: {
				whereString: "sd_id="+sd_id
			},
			method : 'post',
			async: false,
			callback:function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
			}
		});
		Ext.create('Ext.Window', {
			width: width,
			height: height,
			autoShow: true,
			layout: 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/scm/sale/saleDetail.jsp?formCondition=sd_id=' 
					+ sd_id + '&gridCondition=sdd_sdid=' + sd_id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}]
		});
	},
	getCurrentMonth: function(f, type) {
	    Ext.Ajax.request({
	    	url: basePath + 'fa/getMonth.action',
	    	params: {
	    		type: type
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