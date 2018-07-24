Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.GLDetailMulti', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.trigger.DbfindTrigger', 'fa.gla.GLDetailMulti', 'fa.gla.LedgerDetail', 'core.form.MonthDateField', 'core.trigger.CateTreeDbfindTrigger',
            'core.form.ConMonthDateField', 'core.form.ConDateField', 'core.form.YearDateField', 'core.trigger.MultiDbfindTrigger'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=query]': {
    			afterrender: function(btn) {
    				setTimeout(function(){
    					var y = getUrlParam('y'),
    					c = getUrlParam('c'),
    					cr = getUrlParam('cr'),
    					ac = getUrlParam('ac'),
    					un = getUrlParam('un');
    					me.showFilterPanel(btn, y, c, cr, ac, un);
    					me.getCateSetting();
    					if(c && !Ext.isEmpty(c) && c != 'undefined') {
    						Ext.getCmp('prev').setDisabled(true);
    						Ext.getCmp('next').setDisabled(true);
    					}
    					if(y && y != 'undefined') {
    						var fl = Ext.getCmp('query-filter'),
	        					con = me.getCondition(fl);
	        				con.querytype = null;
	        				me.query(con);
    					}
    				}, 200);
    			},
    			click: function(btn) {
    				me.showFilterPanel(btn);
    			}
    		},
    		'button[id=prev]': {
    			click: function(btn) {
    				var fl = Ext.getCmp('query-filter'),
    					con = me.getCondition(fl);
    				con.querytype = 'prev';
    				me.query(con);
    			}
    		},
    		'button[id=next]': {
    			click: function(btn) {
    				var fl = Ext.getCmp('query-filter'),
    					con = me.getCondition(fl);
    				con.querytype = 'next';
    				me.query(con);
    			}
    		},
    		'ledgerdetail': {
    			itemclick: function(selModel, record) {
    				me.loadVoucher(record);
    			}
    		},
    		'button[name=export]': {
    			click: function() {
    				var grid = Ext.getCmp('ledger');
    				me.BaseUtil.exportGrid(grid, '明细账');
    			}
    		},
    		'#ak_asscode': {
    			change: function(f) {
    				if(!Ext.isEmpty(f.value)) {
    					var ch = f.up('window').down('#chkall'),
    					    cd = f.up('window').down('#chkDispLeaf'),
    					    ss = f.up('window').down('#singleshow'),
    					    cl = f.up('window').down('#chkcatelist');
    					ch.setValue(true);
						ch.setReadOnly(true);
    					cd.setValue(true);
    					cd.setReadOnly(true);
    					ss.setValue(true);
    					ss.setReadOnly(true);
    					cl.setReadOnly(true);
    				} else {
    					ch.setReadOnly(false);
    					cd.setReadOnly(false);
    					ss.setReadOnly(false);
    					cl.setReadOnly(false);
    				}
    			}
    		},
    		'#sl_currency': {
    			change: function(f) {
    				var chkcatelist = Ext.getCmp('chkcatelist');
    				if(!Ext.isEmpty(f.value) && f.value == '99') {
    					chkcatelist.setValue(false);
    					chkcatelist.hide();
    				} else {
    					chkcatelist.setValue(false);
    					chkcatelist.show();
    				}
    			}
    		},
    		'#vd_catecode1' : {
    			focus:function(t){
    				var begin = Ext.getCmp("vd_catecode").value, beginname = Ext.getCmp("vd_catename").value;
    				if(Ext.isEmpty(t.value) && !Ext.isEmpty(begin)){
    					t.setValue(begin);
    					Ext.getCmp('vd_catename1').setValue(beginname);
    				}
    			}
    		}
    	});
    },
	getCateSetting : function() {
		var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		params: {
	   			caller: 'Setting',
	   			field: 'se_value',
	   			condition: 'se_what=\'CateTreeSearch\''
	   		},
	   		method : 'post',
	   		async : false,
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			} else if(r.success && r.data == 'true' ){
    				me.CateTreeSearch = true;
	   			}
	   		}
		});
	},
    showFilterPanel: function(btn, ym, caCode, curr, ac, un) {
    	var filter = Ext.getCmp(btn.getId() + '-filter');
    	if(!filter) {
    		filter = this.createFilterPanel(btn);
    	}
    	filter.show();
    	if(ym && ym != 'undefined') {
    		filter.down('#sl_yearmonth').setValue(ym);
    		filter.hide();
    	}
    	if(caCode && !Ext.isEmpty(caCode) && caCode != 'undefined') {
    		filter.down('#ca_code').setValue(caCode);
    	}
    	if(curr && curr != 'undefined') {
    		filter.down('#sl_currency').setValue(curr);
    	}
    	if(ac && ac != 'undefined') {
    		filter.down('#amm_acid').setValue(ac);
    		filter.down('#chkall').setValue(true);
    	}
    	if(un && un != 'undefined') {
    		filter.down('#chkhaveun').setValue(un);
    	}
    },
    hideFilterPanel: function(btn) {
    	var filter = Ext.getCmp(btn.getId() + '-filter');
    	if(filter) {
    		filter.hide();
    	}
    },
    query: function(cond) {
    	var me = this, grid = Ext.getCmp('ledger');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/getGLDetailMulti.action',
    		params: {
    			condition: Ext.encode(cond)
    		},
    		callback: function(opt, s, r) {
    			var res = Ext.decode(r.responseText), 
    				sl_doubledebit = grid.down('gridcolumn[dataIndex=sl_doubledebit]');
    			Ext.getCmp('gl_info_ass').setText('');
    			if(res.exceptionInfo){
    				grid.setLoading(false);
                    showError(res.exceptionInfo);
                }
    			if(res.success){
    				if(cond.sl_currency != '0'){
    					if(!sl_doubledebit) {
    						grid.reconfigure(grid.store, grid.doubleColumns);
    					}
    					if(cond.sl_currency == '99'){
    						grid.down('gridcolumn[dataIndex=sl_doublebalance]').hide();
    					} else {
    						grid.down('gridcolumn[dataIndex=sl_doublebalance]').show();
    					}
    				} else {//显示本币
    					if(sl_doubledebit) {
    						grid.reconfigure(grid.store, grid.defaultColumns);
    					}
    				}
    				if(grid && res.data) {
        				var chkoth = cond.chkoth,/*强制显示对方科目*/
        					chkcatelist = cond.chkcatelist,/*按明细科目列表显示*/
        					cacode = grid.down('gridcolumn[dataIndex=ca_code]'),
        					caname = grid.down('gridcolumn[dataIndex=ca_name]'),
        					sl_othercate = grid.down('gridcolumn[dataIndex=sl_othercate]');
        				grid.store.loadData(res.data);
    					me._updateInfo(cond);
        				if(chkoth) {
        					if(sl_othercate && sl_othercate.hidden)
        						sl_othercate.show();
        				} else {
        					if(sl_othercate && !sl_othercate.hidden)
        						sl_othercate.hide();
        				}
        				if(chkcatelist) {
        					if(cacode && cacode.hidden) {
        						cacode.show();
        						caname.show();
        					}
        				} else {
        					if(cacode && !cacode.hidden) {
        						cacode.hide();
        						caname.hide();
        					}
        				}
        			}
        			grid.setLoading(false);
        		}
    			if(res.filter) {
    				var ca = res.filter.ca_code, ass = res.filter.vds_asscode,
    				    multi = res.filter.assMulti,
    				    chkcatelist = Ext.getCmp('chkcatelist').value;
    				if(ca) {
    					if(ca.lastName) {
    						// 记录当前遍历到的核算编号，下次查询需要用到
    						Ext.getCmp('ca_code').last = ca.last;
    						if(chkcatelist && cond.querytype == null){
    							Ext.getCmp('gl_info_c').setText('科目: '+ca.begin +'到'+ca.end);
    						} else {
    							Ext.getCmp('gl_info_c').setText('科目: '+ca.last+'['+ca.lastName+']');
    						}
    					}
    					if(ca.continuous){
    						if(ca.begin) {
        						Ext.getCmp('vd_catecode').setValue(ca.begin);
        					}
        					if(ca.end) {
        						Ext.getCmp('vd_catecode1').setValue(ca.end);
        					}
    					}
    				}
    				if(ass){
    					if(ass.lastName) {
    						// 记录当前遍历到的核算编号，下次查询需要用到
    						Ext.getCmp('vds_asscode').lastType = ass.lastType;
    						Ext.getCmp('vds_asscode').lastCode = ass.lastCode;
    					} 
    				}
    				if(multi) {
    					Ext.getCmp('assMulti').lastAcid = multi.lastAcid;
						Ext.getCmp('gl_info_ass').setText(multi.lastAssMulti);
					} 
    				Ext.getCmp('iscafirst').setValue(res.filter.iscafirst);
    			}
    		}
    	});
    },
    allPrintBefore: function(cond) {
    	var me = this,
    		grid = Ext.getCmp('ledger');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/getGLDetailPrintData.action',
    		params: {
    			condition: Ext.encode(cond)
    		},
    		callback: function(opt, s, r) {
    			var res = Ext.decode(r.responseText);
    			if(res.success){
    				if(grid && res.filter) {
        				//打印方法
    					var queryId=res.filter.queryId;
    					var currency=Ext.getCmp('sl_currency').value;//币别
    					var chkoth=Ext.getCmp('chkoth').value;//强制显示对方科目
    					var chkcatelist=Ext.getCmp('chkcatelist').value;//按明细科目列表显示
    					var caller='GLDetail!Print',reportName='';
    					if(currency!=0 && chkoth ){//所有币别+强制显示对方科目
    						reportName='GLDetail_all_to';
    					}else if(currency!=0 && chkcatelist){//所有币别+按明细科目列表显示
    						reportName='GLDetail_all_list';
    					}else if(currency!=0){//所有币别
    						reportName='GLDetail_all';
    					}else if(chkoth && chkcatelist){//单个币别+强制显示对方科目+按明细科目列表显示
    						reportName='GLDetail_to_list';
    					}else if(chkoth){//单个币别+强制显示对方科目
    						reportName='GLDetail_to';
    					}else if(chkcatelist){//单个币别+按明细科目列表显示
    						reportName='GLDetail_list';
    					}else{
    						reportName='GLDetail';
    					}
    					var otherParameters=new Object();
    					var params=new Object();
    					 Ext.Ajax.request({
						    	url : basePath +'common/JasperReportPrint/print.action',
								params: {
									params: unescape(escape(Ext.JSON.encode(params))),
									caller:caller,
									reportname:reportName
								},
								method : 'post',
								timeout: 360000,
								callback : function(options,success,response){
									grid.setLoading(false);
									var res = new Ext.decode(response.responseText);
									if(res.success){
										var condition='';
										condition=res.info.whereCondition==""?"where  query_id='"+queryId +"'":"where "+res.info.whereCondition;
										other=Ext.encode(otherParameters);
										var url = res.info.printurl + '?userName='+res.info.userName+'&reportName='+res.info.reportName+'&whereCondition='+condition+'&otherParameters='+other+'&printType='+res.info.printtype;
										window.open(url,'_blank');
									}else if(res.exceptionInfo){
										var str = res.exceptionInfo;
										showError(str);return;
									}
								}
						    });
        			}
        		}
    		}
    	});
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
    	return r;
    },
    _updateInfo: function(r) {
       	var tb = Ext.getCmp('gl_info_ym');
    	if(tb)
    		tb.updateInfo(r);
    },
    loadVoucher: function(record) {
    	var me = this, vc = record.get('sl_vocode');
    	Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		params: {
	   			caller: 'Voucher',
	   			field: 'vo_id',
	   			condition: 'vo_code=\'' + vc + '\''
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var rs = new Ext.decode(response.responseText);
	   			if(rs.exceptionInfo){
	   				showError(rs.exceptionInfo);return null;
	   			}
    			if(rs.success){
    				if(rs.data != null){
    					me.BaseUtil.onAdd('Voucher_' + vc, '凭证', 'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS' + rs.data +
    			    			'&gridCondition=vd_voidIS' + rs.data);
    				}
	   			}
	   		}
		});
    },
    createYearmonthField : function() {
    	return Ext.create('Ext.form.FieldContainer', {
	    	margin: '10 2 2 10',
	    	columnWidth: 1,
	    	height: 70,
	    	layout: 'column',
	    	items: [{
	    		xtype: 'radio',
	    		boxLabel: '按期间查询',
	    		name: 'dateorym',
	    		columnWidth: 0.3,
	    		checked: true,
	    		listeners: {
	    			change: function(f) {
	    				var s = Ext.getCmp('sl_yearmonth');
	    				if(f.checked) {
	    					s.setDisabled(false);
	    				} else {
	    					s.setDisabled(true);
	    				}
	    			}
	    		}
	    	},{
	    		id: 'sl_yearmonth',
				name: 'sl_yearmonth',
				xtype: 'conmonthdatefield',
				columnWidth: .7,
				height: 30,
				getValue: function() {
					if(!Ext.isEmpty(this.value)) {
						return {begin: this.firstVal, end: this.secondVal};
					}
					return null;
				}
	    	},{
	    		xtype: 'radio',
	    		boxLabel: '按日期查询',
	    		name: 'dateorym',
	    		columnWidth: 0.3,
	    		listeners: {
	    			change: function(f) {
	    				var s = Ext.getCmp('sl_date');
	    				if(f.checked) {
	    					s.setDisabled(false);
	    				} else {
	    					s.setDisabled(true);
	    				}
	    			}
	    		}
	    	},{
	    		id: 'sl_date',
				name: 'sl_date',
				xtype: 'condatefield',
				disabled: true,
				columnWidth: .7,
				height: 30,
				getValue: function() {
					if(!Ext.isEmpty(this.value)) {
						return {begin: Ext.Date.toString(this.firstVal), end: Ext.Date.toString(this.secondVal)};
					}
					return null;
				}
	    	}],
	    	getValue: function() {
	    		var a = Ext.getCmp('sl_yearmonth'),b = Ext.getCmp('sl_date');
	    		this.id = b.disabled ? 'sl_yearmonth' : 'sl_date';
	    		return b.disabled ? a.getValue() : b.getValue();
	    	}
    	});
    },
    createCateField : function() {
    	var me = this, t, t1;
    	if (me.CateTreeSearch) {
    		t = Ext.create('erp.view.core.trigger.CateTreeDbfindTrigger', {
				name: 'vd_catecode',
				id: 'vd_catecode',
				autoDbfind: false,
				columnWidth: 0.4,
				listeners: {
					aftertrigger: function(t, d) {
						t.ownerCt.down('#vd_catename').setValue(d[0].raw.data.ca_name);
					}
				}
    		});
    		t1 = Ext.create('erp.view.core.trigger.CateTreeDbfindTrigger', {
				name: 'vd_catecode1',
				id: 'vd_catecode1',
				autoDbfind: false,
				columnWidth: 0.4,
				listeners: {
					aftertrigger: function(t, d) {
						t.ownerCt.down('#vd_catename1').setValue(d[0].raw.data.ca_name);
					}
				}
    		});
    	} else {
    		t = Ext.create('erp.view.core.trigger.DbfindTrigger', {
				id: 'vd_catecode',
				name: 'vd_catecode',
				columnWidth: 0.4,
				listeners: {
					aftertrigger: function(t, d) {
						t.setValue(d.data.ca_code);
						t.ownerCt.down('#vd_catename').setValue(d.data.ca_name);
					}
				}
			});
			t1 = Ext.create('erp.view.core.trigger.DbfindTrigger', {
				id: 'vd_catecode1',
				name: 'vd_catecode1',
				columnWidth: 0.4,
				listeners: {
					aftertrigger: function(t, d) {
						t.setValue(d.data.ca_code);
						t.ownerCt.down('#vd_catename1').setValue(d.data.ca_name);
					}
				}
			});
    	}
    	return Ext.create('Ext.form.FieldContainer', {
			id: 'ca_code',
			margin: '2 2 2 10',
	    	columnWidth: 1,
	    	height: 100,
	    	layout: 'column',
	    	items: [{
	    		xtype: 'radio',
	    		boxLabel: '连续科目范围',
	    		columnWidth: 0.3,
	    		checked: true,
	    		name: 'continueornot',
	    		listeners: {
	    			change: function(f) {
	    				var s = Ext.getCmp('con_vd_catecode');
	    				if(f.checked) {
	    					s.setDisabled(false);
	    				} else {
	    					s.setDisabled(true);
	    				}
	    			}
	    		}
	    	},{
				labelWidth: 80,
				layout: 'column',
				columnWidth: 0.7,
				height: 56,
				xtype: 'fieldcontainer',
				id: 'con_vd_catecode',
				defaults: {
					fieldStyle : "background:#FFFAFA;color:#515151;"
				},
				items: [t, {
					xtype: 'textfield',
					name: 'vd_catename',
					id: 'vd_catename',
					columnWidth: 0.6,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				},t1 ,{
					xtype: 'textfield',
					name: 'vd_catename1',
					id: 'vd_catename1',
					columnWidth: 0.6,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				}],
				getValue: function() {
					var a = this.down('#vd_catecode').value,b = this.down('#vd_catecode1').value,
						x = Ext.isEmpty(a), y = Ext.isEmpty(b);
					if(x && y) {
						return null;
					} else if(x && !y) {
						a = b;
					} else if(!x && y){
						b = a;
					}
					return {begin: a, end: b};
				}
	    	},{
	    		xtype: 'radio',
	    		boxLabel: '非连续科目范围',
	    		columnWidth: 0.3,
	    		name: 'continueornot',
	    		listeners: {
	    			change: function(f) {
	    				var s = Ext.getCmp('un_vd_catecode');
	    				if(f.checked) {
	    					s.setDisabled(false);
	    				} else {
	    					s.setDisabled(true);
	    				}
	    			}
	    		}
	    	},{
	    		xtype: 'cateTreeDbfindTrigger',
	    		mode: 'MULTI',
	    		id: 'un_vd_catecode',
				name: 'un_vd_catecode',
				disabled: true,
				columnWidth: 0.7
	    	}],
	    	getValue: function() {
	    		var a = Ext.getCmp('con_vd_catecode'),b = Ext.getCmp('un_vd_catecode');
	    		var val = b.disabled ? a.getValue() : b.value;
	    		if(val) {
	    			return b.disabled ? {continuous: true, begin: val.begin, end:val.end, last: this.last} : {continuous: false, begin: val, end:null, last: this.last};
	    		}
    			return b.disabled ? {continuous: true, begin: null, end:null, last: this.last} : {continuous: false, begin: null, end:null, last: this.last};
	    	},
	    	setValue: function(v) {
	    		Ext.getCmp('vd_catecode').setValue(v);
	    		Ext.getCmp('vd_catecode1').setValue(v);
	    	}
    	});
    },
    createCateLevelField : function() {
    	return Ext.create('Ext.form.FieldContainer', {
    		fieldLabel: '科目级别',
			labelWidth: 80,
			height: 23,
			layout: 'hbox',
			columnWidth: .5,
			id: 'ca_level',
			defaults: {
				fieldStyle : "background:#FFFAFA;color:#515151;"
			},
			items: [{
				xtype: 'combo',
				flex: 1,
				editable: false,
				name: 'ca_level1',
				id: 'ca_level1',
				displayField: 'display',
				valueField: 'value',
				queryMode: 'local',
				value: 1,
				store: Ext.create('Ext.data.Store', {
		            fields: ['display', 'value'],
		            data : [
		                {"display": 1, "value": 1},
		                {"display": 2, "value": 2},
		                {"display": 3, "value": 3},
		                {"display": 4, "value": 4},
		                {"display": 5, "value": 5}
		            ]
		        })
			},{
				xtype: 'combo',
				flex: 1,
				name: 'ca_level2',
				id: 'ca_level2',
				editable: false,
				displayField: 'display',
				valueField: 'value',
				queryMode: 'local',
				value: 5,
				store: Ext.create('Ext.data.Store', {
		            fields: ['display', 'value'],
		            data : [
		                {"display": 1, "value": 1},
		                {"display": 2, "value": 2},
		                {"display": 3, "value": 3},
		                {"display": 4, "value": 4},
		                {"display": 5, "value": 5}
		            ]
		        })
			}],
			getValue: function() {
				var a = this.down('#ca_level1').value,b = this.down('#ca_level2').value;
				return {begin: Math.min(a, b), end: Math.max(a, b)};
			},
			setValue: function() {
				
			}
    	});
    },
    createCurrencyField : function() {
    	var me = this;
    	return Ext.create('Ext.form.field.ComboBox', {
    		fieldLabel: '币别',
			labelWidth: 80,
			height: 23,
			layout: 'hbox',
			columnWidth: .5,
			id: 'sl_currency',
			queryMode: 'local',
			displayField: 'display',
			valueField: 'value',
			editable: false,
			store: Ext.create('Ext.data.Store', {
	            fields: ['display', 'value'],
	            data : [
	                {"display": '本位币', "value": '0'},
	                {"display": '所有币别', "value": '99'}
	            ]
	        }),
	        value: '0',
			listeners: {
				afterrender: function(f) {
					me.getCurrency(f);
				}
			}
    	});
    },
    createAssField : function() {
    	return Ext.create('Ext.form.FieldContainer', {
    		fieldLabel: '辅助核算',
			labelWidth: 80,
			height: 23,
			layout: 'hbox',
			columnWidth: 1,
			id: 'vds_asscode',
			defaults: {
				fieldStyle : "background:#FFFAFA;color:#515151;"
			},
			items: [{
				labelWidth: 35,
				xtype: 'dbfindtrigger',
				flex: 0.2,
				id: 'ak_name',
				name: 'ak_name',
	    		listeners: {
	    			aftertrigger: function(f, r) {
	    				if(!Ext.isEmpty(f.value)) {
	    					f.asskind = r.data;
	    					var c = Ext.getCmp('ak_asscode'),
	    						n = Ext.getCmp('ak_assname');
	    					c.name = r.data.ak_asscode;
	    					n.name = r.data.ak_assname;
	    				} else {
	    					f.asskind = null;
	    				}
	    			},
	    			change: function(f) {
	    				var a = Ext.getCmp('ak_asscode'),
	    					b = Ext.getCmp('ak_assname');
	    				a.setValue(null);
	    				b.setValue(null);
	    				if(Ext.isEmpty(f.value)) {
	    					f.asskind = null;
	    				}
	    			}
	    		}
			},{
				xtype: 'dbfindtrigger',
				id: 'ak_asscode',
				flex: 0.2,
				listeners: {
					beforetrigger: function(f) {
						var a = Ext.getCmp('ak_name');
						f.dbBaseCondition = null;
						if(!a.asskind) {
							a.focus(false, 200);
							showError('请先选择核算项!');
							return false;
						} else if(f.name == 'akd_asscode') {
							f.dbBaseCondition = 'ak_name=\'' + a.value + '\'';
						}
					},
					aftertrigger: function(f, r) {
						var a = f.ownerCt.down('#ak_name'),
							n = Ext.getCmp('ak_assname');
						if(a.asskind) {
							f.setValue(r.data[f.name]);
							n.setValue(r.data[n.name]);
						}
	    			}
				}
			},{
				xtype: 'textfield',
				id: 'ak_assname',
				name: 'ak_assname',
				flex: 0.6,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			}],
			getValue: function() {
				var a = Ext.getCmp('ak_name'),
					c = Ext.getCmp('ak_asscode'),
					n = Ext.getCmp('ak_assname');
				if(a.asskind) {
					return {asl_asstype: a.asskind.ak_name, asl_asscode: c.value, asl_assname: n.value, lastType: this.lastType, lastCode: this.lastCode};
				}
				return {asl_asstype: null, asl_asscode: null, asl_assname: null, lastType: this.lastType, lastCode: this.lastCode};
			}
    	});
    },
    createFilterPanel: function(btn) {
    	var me = this;
    	var filter = Ext.create('Ext.Window', {
    		id: btn.getId() + '-filter',
    		style: 'background:#f1f1f1',
    		title: '筛选条件',
    		width: 530,
    		height: 500,
    	    layout: 'column',
    	    defaults: {
    	    	margin: '2 2 2 10'
    	    },
    	    items: [me.createYearmonthField(), me.createCateField(), me.createCurrencyField(), 
    	            me.createCateLevelField(), me.createAssField(), {
				xtype: 'checkbox',
				id: 'chkall',
				name: 'chkall',
				columnWidth: .5,
				boxLabel: '显示辅助核算',
				listeners: {
					change: function(f){
						var s = Ext.getCmp('singleshow'), c = Ext.getCmp('chkDispLeaf');
						if(f.checked && c.checked) {
							s.setValue(false);
							s.setDisabled(false);
						} else {
							s.setValue(false);
							s.setDisabled(true);
						}
					}
				}
			},{
				xtype: 'checkbox',
				id: 'chkdis',
				name: 'chkdis',
				columnWidth: .5,
				boxLabel: '显示禁用科目'
			},{
				xtype: 'checkbox',
				id: 'chkhaveun',
				name: 'chkhaveun',
				columnWidth: .5,
				boxLabel: '包含未记账凭证',
				checked: true
			},{
				xtype: 'checkbox',
				id: 'chkDispLeaf',
				name: 'chkDispLeaf',
				columnWidth: .5,
				boxLabel: '只显示末级科目',
				listeners: {
					change: function(f){
						var s = Ext.getCmp('singleshow'), c = Ext.getCmp('chkall'), l = Ext.getCmp('chkcatelist');
						if(f.checked) {
							l.setValue(false);
							if(c.checked){
								s.setValue(false);
								s.setDisabled(false);
							}
						} else {
							s.setValue(false);
							s.setDisabled(true);
						}
					}
				}
			},{
				xtype: 'checkbox',
				id: 'chkno',
				name: 'chkno',
				columnWidth: .5,
				boxLabel: '无发生额不显示',
				listeners: {
					change: function(f){
						var s = Ext.getCmp('chkzeroandno');
						if(f.checked) {
							s.setValue(false);
							s.setDisabled(true);
						} else {
							s.setValue(false);
							s.setDisabled(false);
						}
					}
				}
			},{
				xtype: 'checkbox',
				id: 'singleshow',
				name: 'singleshow',
				columnWidth: .5,
				boxLabel: '带核算项目的末级科目不单独显示',
				disabled: true
			},{
				xtype: 'checkbox',
				id: 'chkzeroandno',
				name: 'chkzeroandno',
				columnWidth: .5,
				boxLabel: '余额为零且无发生额不显示',
				checked: true
			},{
				xtype: 'checkbox',
				id: 'chkoth',
				name: 'chkoth',
				columnWidth: .5,
				boxLabel: '强制显示对方科目',
				checked: true
			},{
				xtype: 'checkbox',
				id: 'chkcatelist',
				name: 'chkcatelist',
				columnWidth: .5,
				boxLabel: '按明细科目列表显示',
				listeners: {
					change: function(f){
						var s = Ext.getCmp('singleshow'), c = Ext.getCmp('chkDispLeaf');
						if(f.checked) {
							c.setValue(false);
							s.setValue(false);
							s.setDisabled(true);
						} else {
							s.setValue(false);
							s.setDisabled(true);
						}
					}
				}
			},{
				xtype: 'checkbox',
				id: 'chkothasslist',
				name: 'chkothasslist',
				columnWidth: .5,
				boxLabel: '按对方科目多条显示',
				listeners: {
					change: function(f){
						var s = Ext.getCmp('chkoth');
						if(f.checked) {
							s.setValue(true);
						}
					}
				}
			},{
				xtype: 'fieldcontainer',
				fieldLabel : '排序方法',
				id: 'operator',
				name: 'operator',
				defaultType: 'radiofield',
				columnWidth: 1,
				layout: 'hbox',
				defaults: {
	                flex: 1
	            },
	            items: [{
	            	boxLabel  : '凭证号，日期',
	                inputValue: 'numdate',
	                name: 'operator',
	                checked: true
	            }, {
	                boxLabel  : '日期， 凭证号',
	                inputValue: 'datenum',
	                name: 'operator'
	            }],
	            getValue:function(){
	            	var operator = this.down('radio[checked=true]');
	            	return operator.inputValue;
	            }
			},{
				xtype: 'fieldcontainer',
				id: 'assMulti',
				name: 'assMulti',
				defaultType: 'displayfield',
				columnWidth: 1,
	            items: [{
	            	id: 'amm_acid',
					name: 'amm_acid',
	            }],
				getValue: function() {
					var a = Ext.getCmp('amm_acid').value;
					if(a) {
						return {amm_acid: a, lastAcid: this.lastAcid};
					}
					return {amm_acid: null, lastAcid: this.lastAcid};
				}
			},{
				xtype : 'textfield',
				id: 'iscafirst',
				name: 'iscafirst',
				columnWidth: .5,
				value: 0,
				hidden: true
			}],
    	    buttonAlign: 'center',
    	    buttons: [{
	    		text: '确定',
	    		width: 60,
	    		cls: 'x-btn-gray',
	    		handler: function(btn) {
	    			var fl = Ext.getCmp('query-filter'),
						con = me.getCondition(fl);
	    			con.querytype = null;
	    			me.query(con);
	    			fl.hide();
	    		}
	    	},{
	    		text: '打印',
	    		width: 60,
	    		cls: 'x-btn-gray',
	    		handler: function(btn) {
	    			var fl = Ext.getCmp('query-filter'),
					con = me.getCondition(fl);
	    			con.querytype = null;
	    			me.allPrintBefore(con);
	    		}
	    	},{
	    		text: '关闭',
	    		width: 60,
	    		cls: 'x-btn-gray',
	    		handler: function(btn) {
	    			var fl = btn.ownerCt.ownerCt;
	    			fl.hide();
	    		}
	    	}]
    	});
    	this.getCurrentMonth(filter.down('#sl_yearmonth'));
		return filter;
    },
    getCurrency: function(f) {
    	Ext.Ajax.request({
    		url : basePath + 'common/getFieldDatas.action',
       		async: false,
       		params: {
       			caller: 'Currencys',
       			field: 'cr_name',
       			condition: 'cr_statuscode=\'CANUSE\''
       		},
       		method : 'post',
       		callback : function(options,success,response){
       			var rs = new Ext.decode(response.responseText);
       			if(rs.exceptionInfo){
       				showError(rs.exceptionInfo);return null;
       			}
    			if(rs.success && rs.data){
    				var cr = rs.data.split('#'),c = new Array();
    				Ext.each(cr, function(r){
    					c.push({display: r, value: r});
    				});
    				f.store.add(c);
    			}
       		}
    	});
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
    				var month=String(rs.data.PD_DETNO).substring(0,6);
    				f.setValue(month);
    			}
    		}
    	});
    }
});