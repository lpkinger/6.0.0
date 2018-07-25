Ext.QuickTips.init();
Ext.define('erp.controller.fa.RepQuery', {
    extend: 'Ext.app.Controller',
    id: 'printcwform',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['fa.RepQuery', 'core.button.Query', 'core.button.Export', 'core.button.Print', 'core.button.Close',
            'core.form.MonthDateField','core.trigger.DbfindTrigger'],
    init:function(){
    	var me = this;
    	this.control({
    		'#form': {
    			afterrender: function(form) {
    				me.BaseUtil.getSetting('sys', 'auditDuring', function(bool) {
    					if(bool) {
    						form.down('#frd_yearmonth').hide();
    						form.down('#addate').show();
    						me.getCurrentMonth(form.down('#addate'));
    					} else {
    						form.down('#addate').hide();
    						form.down('#frd_yearmonth').show();
    						me.getCurrentMonth(form.down('#frd_yearmonth'));
    					}
    	            });
    			}
    		},
    		'#fs_code': {
    			afterrender: function(f) {
    				me.getRepCodes(f);
    			},
    			change: function(f) {
    				if(!Ext.isEmpty(f.value)) {
    					Ext.defer(function(){
    						var grid = f.ownerCt.ownerCt.down('grid');
    						me.resetGrid(grid);
    					}, 200);
    				}
    			}
    		},
    		/*'combo[name=fs_name]': {
    			afterrender: function(f) {
    				me.getRepCodes(f);
    			},
    			change: function(f) {
    				if(!Ext.isEmpty(f.value)) {
    					Ext.defer(function(){
    						var d = f.lastSelection[0].data.data,
    							form = f.ownerCt;
    						form.down('textfield[name=fs_code]').setValue(d.FS_CODE);
    						var grid = form.ownerCt.down('grid');
    						me.resetGrid(grid, d);
    					}, 200);
    				}
    			}
    		},*/
    		/*'monthdatefield': {
				afterrender: function(f) {
					this.getCurrentMonth(f);
				}
			},*/
			'button[name=export]': {
				click: function(btn) {
					var grid = btn.ownerCt.ownerCt.ownerCt.down('gridpanel');
					var fsname = Ext.getCmp('fs_name').value;
					var fsyearmonth = Ext.getCmp('frd_yearmonth');
					if(!fsyearmonth||fsyearmonth.isHidden()){
						fsyearmonth = Ext.getCmp('addate');
					}
					var yearmonth = fsyearmonth.getValue();
					this.BaseUtil.exportGrid(grid,null,fsname+'('+yearmonth+')');
				}
			},
			'erpQueryButton': {
				click: function(b) {
					var grid = b.ownerCt.ownerCt.ownerCt.down('gridpanel');
					this.getGridData(grid);
				}
			}
			,
			'erpPrintButton': {
				click: function(btn) {
				    var form = Ext.getCmp('printcwform');
				  //  var title=form.title;
				    if(Ext.getCmp('fs_code') && Ext.getCmp('fs_code').value =='P01'){
				    	var reportName="FAReportSY";
				    }else{
				    	console.log(reportName);
				    	var reportName="FAReport";
				    }
					var condition='{fareport.fr_fscode}='+ "'"+Ext.getCmp('fs_code').value+"'"+' and '+'{fareport.fr_yearmonth}='+Ext.getCmp('frd_yearmonth').value;
					
					//me.FormUtil.batchPrint(title,reportName,condition,'','','','','','','');
				   	 //在这里传条件和报表名字
					
					//=======================================================
		    	 	var me = this;
			   		Ext.Ajax.request({
				   		url : basePath + 'common/enterprise/getprinturl.action?caller=' + caller,
				   		callback: function(opt, s, r) {
				   			var re = Ext.decode(r.responseText);
				   			thisreport=re.reportname;
				   			//===========================================
				   			var whichsystem = re.whichsystem;
							var urladdress = "";
							var rpname = re.reportName;
							if(Ext.isEmpty(rpname) || rpname == "null"){
								urladdress = re.printurl;
							} else if(rpname.indexOf(thisreport) > 0){
								urladdress = re.ErpPrintLargeData;
							} else{
								urladdress = re.printurl;
							}
						   	me.FormUtil.batchPrint('',reportName,condition,'','','','','',urladdress,whichsystem);
						   	 //在这里传条件和报表名字
				   		}
			   		});
					
				}
			}
    	});
    },
    getRepCodes: function(f) {
    	Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		params: {
	   			caller: 'FARepSet',
	   			fields: 'fs_code,fs_name,fs_title1,fs_title2,fs_righttitle1,fs_righttitle2,fs_head,fs_righthead',
	   			condition: '1=1 order by fs_code'
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
	   				showError(rs.exceptionInfo);return;
	   			}
    			if(rs.success){
    				var data = Ext.decode(rs.data)[0];
    				f.setValue(data.FS_CODE);
    				Ext.getCmp('fs_name').setValue(data.FS_NAME);
    				Ext.getCmp('fs_head').setValue(data.FS_HEAD);
    				Ext.getCmp('fs_title1').setValue(data.FS_TITLE1);
    				Ext.getCmp('fs_title2').setValue(data.FS_TITLE2);
    				Ext.getCmp('fs_righttitle1').setValue(data.FS_RIGHTTITLE1);
    				Ext.getCmp('fs_righttitle2').setValue(data.FS_RIGHTTITLE2);
    				Ext.getCmp('fs_righthead').setValue(data.FS_RIGHTHEAD);
	   			}
	   		}
		});
    },
    resetGrid: function(grid) {
    	var columns = [{
    		text: Ext.getCmp('fs_head').value,
    		dataIndex: 'frd_name',
    		cls: 'x-grid-header-1',
    		flex: 1
    	}, {
    		text: '行次',
    		dataIndex: 'frd_step',
    		cls: 'x-grid-header-1',
    		flex: 0.3
    	}, {
    		text: Ext.getCmp('fs_title1').value,
    		dataIndex: 'frd_amount1',
    		cls: 'x-grid-header-1',
    		xtype: 'numbercolumn',
    		format: '0,000.00',
    		align: 'right',
    		flex: 1,
    		renderer: function(v, m, r) {
    			if (v == 0) {
    				m.style = 'text-align: center;color:red;';
    				return '-';
    			}
    			return Ext.util.Format.number(v, '0,000.00');
    		}
    	}, {
    		text: Ext.getCmp('fs_title2').value,
    		dataIndex: 'frd_amount2',
    		cls: 'x-grid-header-1',
    		xtype: 'numbercolumn',
    		format: '0,000.00',
    		align: 'right',
    		flex: 1,
    		renderer: function(v, m, r) {
    			if (v == 0) {
    				m.style = 'text-align: center;color:red;';
    				return '-';
    			}
    			return Ext.util.Format.number(v, '0,000.00');
    		}
    	},{
    		text: Ext.getCmp('fs_righthead').value,
    		dataIndex: 'frd_rightname',
    		cls: 'x-grid-header-1',
    		flex: 1
    	}, {
    		text: '行次',
    		dataIndex: 'frd_rightstep',
    		cls: 'x-grid-header-1',
    		flex: 0.3
    	}, {
    		text: Ext.getCmp('fs_righttitle1').value,
    		dataIndex: 'frd_rightamount1',
    		cls: 'x-grid-header-1',
    		xtype: 'numbercolumn',
    		format: '0,000.00',
    		align: 'right',
    		flex: 1,
    		renderer: function(v, m, r) {
    			if (v == 0) {
    				m.style = 'text-align: center;color:red;';
    				return '-';
    			}
    			return Ext.util.Format.number(v, '0,000.00');
    		}
    	}, {
    		text: Ext.getCmp('fs_righttitle2').value,
    		dataIndex: 'frd_rightamount2',
    		cls: 'x-grid-header-1',
    		xtype: 'numbercolumn',
    		format: '0,000.00',
    		align: 'right',
    		flex: 1,
    		renderer: function(v, m, r) {
    			if (v == 0) {
    				m.style = 'text-align: center;color:red;';
    				return '-';
    			}
    			return Ext.util.Format.number(v, '0,000.00');
    		}
    	}];
    	var store = new Ext.data.Store({
    		fields: ['frd_name', 'frd_step', 'frd_amount1', 'frd_amount2', 'frd_rightname', 'frd_rightstep',
    		         'frd_rightamount1', 'frd_rightamount2'],
    		data: [{frd_step: 1},{frd_step: 2},{frd_step: 3},{frd_step: 4},{frd_step: 5}]
    	});
    	grid.reconfigure(store, columns);
    	this.getGridData(grid);
    },
    getCurrentMonth: function(f) {
	    Ext.Ajax.request({
	    	url: basePath + 'fa/getMonth.action',
	    	params: {
	    		type: 'MONTH-A'
	    	},
	    	callback: function(opt, s, r) {
	    		var rs = Ext.decode(r.responseText);
	    		if(rs.data) {
	    			f.setValue(rs.data.PD_DETNO);
	    		}
	    	}
	    });
	},
	getGridData: function(grid) {
		var me = this, ym;
		me.BaseUtil.getSetting('sys', 'auditDuring', function(bool) {
			if(bool) {
				ym = Ext.getCmp('addate').value;
				me.queryGridDetail(grid, ym);
			} else {
				ym = Ext.getCmp('frd_yearmonth').value;
				me.queryGridDetail(grid, ym);
			}
        });
	},
	queryGridDetail: function(grid, ym){
		var code = Ext.getCmp('fs_code').value,
		cond = '';
	if(!Ext.isEmpty(code)) {
		cond = "frd_fscode='" + code + "'";
	}
	if(!Ext.isEmpty(ym)) {
		if(cond.length > 0)
			cond += ' AND frd_yearmonth=' + ym;
		else
			cond = 'frd_yearmonth=' + ym;
	}
	if(cond.length == 0) {
		cond = '1=1';
	}
	cond += ' order by to_number(frd_step)';
	Ext.Ajax.request({
   		url : basePath + 'common/getFieldsDatas.action',
   		params: {
   			caller: 'FAReportDetail',
   			fields: 'distinct frd_name, frd_step, frd_rate*frd_amount1 frd_amount1, frd_rate*frd_amount2 frd_amount2, frd_rightname, frd_rightstep, frd_rightrate*frd_rightamount1 frd_rightamount1, frd_rightrate*frd_rightamount2 frd_rightamount2',
   			condition: cond
   		},
   		method : 'post',
   		callback : function(options,success,response){
   			var rs = new Ext.decode(response.responseText);
   			if(rs.exceptionInfo){
   				showError(rs.exceptionInfo);return;
   			}
			if(rs.success){
				var data = Ext.decode(rs.data), s = [];
				Ext.each(data, function(d){
					s.push({
						frd_name: d.FRD_NAME,
						frd_step: d.FRD_STEP,
						frd_amount1: d.FRD_AMOUNT1,
						frd_amount2: d.FRD_AMOUNT2,
						frd_rightname: d.FRD_RIGHTNAME,
						frd_rightstep: d.FRD_RIGHTSTEP,
						frd_rightamount1: d.FRD_RIGHTAMOUNT1,
						frd_rightamount2: d.FRD_RIGHTAMOUNT2
					});
				});
				grid.store.loadData(s);
   			}
   		}
	});
	}
});