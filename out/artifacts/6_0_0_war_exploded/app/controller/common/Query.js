Ext.QuickTips.init();
Ext.define('erp.controller.common.Query', {
	extend : 'Ext.app.Controller',
	views : ['common.query.Viewport', 'common.query.GridPanel', 'common.query.Form', 'core.trigger.AddDbfindTrigger','core.trigger.DbfindTrigger','core.form.FtNumberField',
			'core.form.FtField', 'core.form.ConDateField', 'core.form.YnField', 'core.form.FtDateField','core.form.YearDateField',
			'core.form.MonthDateField','core.form.FtFindField', 'core.grid.YnColumn', 'core.grid.TfColumn', 'core.button.Refresh',
			'core.form.ConMonthDateField', 'core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger' ],
	init : function() {
		var me=this;
		var emcode = null;
		var value='';
		this.control({
			'erpQueryFormPanel' : {
				beforerender : function(form){
				},
				alladded : function(form) {
					var items = form.items.items, autoQuery = false;
					/**
					 * 考勤数据分析界面跳转到考勤汇总表所带的参数
					 */
					var startdate = getUrlParam("startdate");
					var enddate = getUrlParam("enddate");
					var comboValue = getUrlParam("comboValue");
					/**
					 * 考勤汇总跳转到考勤日报所带的参数
					 */
					var start = getUrlParam("start");
					var end = getUrlParam("end");
					var record = getUrlParam("record");
					var comboValue2 = getUrlParam("comboValue2");
					if(caller=='AttendDataTotal'&&startdate!=null && enddate!=null){//考勤数据分析界面跳转到考勤汇总表
						var ad_indate = Ext.getCmp("ad_indate");
						var from = Ext.getCmp(ad_indate.name + '_from');
		        		var to = Ext.getCmp(ad_indate.name + '_to');
						ad_indate.setValue(comboValue);
						from.setValue(new Date(startdate));
						to.setValue(new Date(enddate));
						ad_indate.firstVal = new Date(startdate);
						ad_indate.secondVal = new Date(enddate);
						from.setEditable(false);
						to.setEditable(false);
						form.onQuery();
					}
					if(caller=="AttendDataQuery"&&record!=null){//考勤汇总条转到考勤日报
						var cont = parent.Ext.getCmp('content-panel');
						cont.getActiveTab().setTitle("考勤日报表");
						var ad_indate = Ext.getCmp("ad_indate");
						var from = Ext.getCmp(ad_indate.name + '_from');
		        		var to = Ext.getCmp(ad_indate.name + '_to');
						ad_indate.setValue(comboValue2);
						from.setValue(new Date(start));
						to.setValue(new Date(end));
						ad_indate.firstVal = new Date(start);
						ad_indate.secondVal = new Date(end);
						from.setEditable(false);
						to.setEditable(false);
						var record_ = record.split(",");
						Ext.getCmp("ad_emcode").setValue(record_[0]);
						if(record_[1]>0){
							Ext.getCmp("ad_latemin").setValue(1);
						}
						if(record_[2]>0){
							Ext.getCmp("ad_leaveearlymin").setValue(1);
						}
						if(record_[3]>0){
							Ext.getCmp("ad_absentmin").setValue(1);
						}
						form.onQuery();
					}
					Ext.each(items, function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val)) {
							this.setValue(val);
							autoQuery = true;
							if(caller == 'GoodsOnSale'){
								
							}else if(this.xtype == 'dbfindtrigger') {
								this.autoShowTriggerWin=false;
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
			'button[name=refresh]':{
    			click: function(btn){   
    				var form = me.getForm(btn);
    				if (caller == 'ALMonth!Query'){
    					 var month = Ext.getCmp('am_yearmonth');
    					 var condition=month.secondVal;
    					 Ext.Ajax.request({
        					url: basePath + 'fa/ars/CmQueryController/refreshQuery.action',
        					params: {
        						condition:condition
    						},
        					method: 'post',        					
        					callback: function(opt, s, r) {        						
        						var rs = Ext.decode(r.responseText);
        						if(rs.success) {
        								form.onQuery();        							
        						}else{
        							
        						}
        					}
        				});
    				}else{
    					form.onQuery();
    				}
    			}
			},
			'erpQueryGridPanel' : {
				itemclick : this.onGridItemClick,
				afterrender:function(grid,c){
					if(caller=='AttendDataTotal'){
						Ext.Array.each(grid.columns,function(column){
							if(column.dataIndex=='ad_kind' || column.dataIndex=='ad_leaveearlymin' || column.dataIndex=='ad_latemin'){//
								column.renderer = function(val,meta, record, x, y, store, view){
									value = record.data.ad_emcode+","+record.data.ad_latemin+","+record.data.ad_leaveearlymin+","+record.data.ad_onovertime;
									if(val==0){
										return;
									}
									var start=Ext.Date.format(Ext.getCmp('ad_indate_from').getValue(),'Y-m-d');
						    		var end=Ext.Date.format(Ext.getCmp('ad_indate_to').getValue(),'Y-m-d');
									var url_ = "jsps/common/query.jsp?whoami=AttendDataQuery&record="+value+"&start="+start+"&end="+end+'&comboValue2='+Ext.getCmp('ad_indate').combo.value;
									return '<a href="javascript:openUrl(\'' + url_ + '\');">' + val + '</a>';
								}
							}
						});
					}
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
					if(f.name == 'am_yearmonth') {
						type = 'MONTH-B';
					}
					if(f.name == 'vd_yearmonth') {
						type = 'MONTH-A';
					}
					if(f.name == 'pwm_yearmonth') {
						type = 'MONTH-P';
					}
					if(type != '' && Ext.isEmpty(getUrlParam(f.name))) {
						this.getCurrentMonth(f, type);
					}
				}
			},
			'conmonthdatefield': {
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
					if(f.name == 'am_yearmonth') {
						type = 'MONTH-B';
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
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
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