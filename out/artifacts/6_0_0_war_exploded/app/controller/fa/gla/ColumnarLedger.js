Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.ColumnarLedger', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views: ['core.trigger.DbfindTrigger', 'fa.gla.ColumnarLedger', 'fa.gla.ColumnarLedgerDetail', 'core.form.MonthDateField', 
            'core.form.ConMonthDateField', 'core.form.YearDateField', 'core.trigger.CateTreeDbfindTrigger'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=query]': {
    			afterrender: function(btn) {
    				setTimeout(function(){
    					me.showFilterPanel(btn);
    				}, 200);
    			},
    			click: function(btn) {
    				me.showFilterPanel(btn);
    			}
    		},
    		'columnarledgerdetail': {
    			itemclick: function(selModel, record) {
    				me.loadVoucher(record);
    			}
    		},
    		'button[name=export]': {
    			click: function() {
    				var grid = Ext.getCmp('columnarledgerdetail'), mas_name = Ext.getCmp('mas_name').value;
    				me.BaseUtil.exportGrid(grid, mas_name);
    			}
    		},
    		'button[id=first]': {
    			click: function(btn) {
    				var fl = Ext.getCmp('query-filter'),
    					con = me.getCondition(fl);
    				con.querytype = 'first';
    				me.query(con);
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
    		'button[id=end]': {
    			click: function(btn) {
    				var fl = Ext.getCmp('query-filter'),
    					con = me.getCondition(fl);
    				con.querytype = 'end';
    				me.query(con);
    			}
    		},
    		'#ak_name': {
    			change: function(f) {
    				var chkall = f.up('window').down('#chkall'), assbranch = f.up('window').down('#assbranch');
    				if(f.value){
    					chkall.setValue(true);
    					assbranch.setValue(false);
    					assbranch.setReadOnly(false);
    				} else {
    					chkall.setValue(false);
    					assbranch.setValue(false);
    					assbranch.setReadOnly(true);
    				}
    			}
    		},
    		'#mas_assistant': {
    			change: function(f) {
    				var assbranch = f.up('window').down('#assbranch');
    				if(f.value != 0){
    					assbranch.setValue(false);
    					assbranch.hide();
    				} else {
    					assbranch.setValue(false);
    					assbranch.show();
    				}
    			}
    		},
    		'#assbranch': {
    			change: function(f) {
    				if(f.value){
    					Ext.getCmp('first').show();
    					Ext.getCmp('prev').show();
        				Ext.getCmp('next').show();
        				Ext.getCmp('end').show();
    				} else {
    					Ext.getCmp('first').hide();
        				Ext.getCmp('prev').hide();
        				Ext.getCmp('next').hide();
        				Ext.getCmp('end').hide();
    				}
    			},
    			afterrender: function(f) {
    				if(f.value){
    					Ext.getCmp('first').show();
    					Ext.getCmp('prev').show();
        				Ext.getCmp('next').show();
        				Ext.getCmp('end').show();
    				} else {
    					Ext.getCmp('first').hide();
        				Ext.getCmp('prev').hide();
        				Ext.getCmp('next').hide();
        				Ext.getCmp('end').hide();
    				}
    			},
    		}
    	});
    },
    showFilterPanel: function(btn, assCode) {
    	var filter = Ext.getCmp(btn.getId() + '-filter');
    	if(!filter) {
    		filter = this.createFilterPanel(btn);
    	}
    	filter.show();
    },
    hideFilterPanel: function(btn) {
    	var filter = Ext.getCmp(btn.getId() + '-filter');
    	if(filter) {
    		filter.hide();
    	}
    },
    query: function(cond) {
    	var grid = Ext.getCmp('columnarledgerdetail');
    	grid.reconfigure(grid.store, grid.defaultColumns);
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/getColumnarLedger.action',
    		params: {
    			condition: Ext.encode(cond)
    		},
    		callback: function(opt, s, r) {
    			grid.setLoading(false);
    			var res = Ext.decode(r.responseText), 
    				sl_doubledebit = grid.down('gridcolumn[dataIndex=sl_doubledebit]');
    			if(res.success){
    				if(cond.sl_currency != '0') {//显示原币
    					if(!sl_doubledebit) {
    						grid.reconfigure(grid.store, grid.doubleColumns);
    					}
    				} else {//只显示本币
    					if(sl_doubledebit) {
    						grid.reconfigure(grid.store, grid.defaultColumns);
    					}
    				}
					if(res.columns) {
						var debitCols = res.columns[0], creditCols = res.columns[1];
						if(debitCols && debitCols.length > 0){
							grid.headerCt.add({
								cls: 'x-grid-header-1',
		    					text: '借方',
		    					columns: debitCols
		    				});  
						}
						if(creditCols && creditCols.length > 0){
							grid.headerCt.add({
								cls: 'x-grid-header-1',
		    					text: '贷方',
		    					columns: creditCols
		    				});  
						}					
					}
	    			if(res.data) {
	    				grid.store.loadData(res.data);
	    			}
	    			if(res.filter) {
	    				var ass = res.filter.ass_code;
	    				if(ass) {
	    					if(cond.assbranch) {//辅助核算分页显示
		    					if(ass.currentName) {
		    						// 记录当前遍历到的核算编号，下次查询需要用到
		    						Ext.getCmp('ass_code').current = ass.current;
		    						Ext.getCmp('gl_info_ass').setText('核算项: ['+cond.ak_name+']--('+ass.current+')'+ass.currentName);
		    					} 
	    					} else {
	    						Ext.getCmp('gl_info_ass').setText('');
	    					}
	    					// 后端返回的是经确认后的核算编号区间，前端直接重置即可
	    					if(ass.begin) {
	    						Ext.getCmp('ak_asscode').setValue(ass.begin);
	    						Ext.getCmp('ak_assname').setValue(ass.beginName);
	    					}
	    					if(ass.end) {
	    						Ext.getCmp('ak_asscode1').setValue(ass.end);
	    						Ext.getCmp('ak_assname1').setValue(ass.endName);
	    					}
	    				}
	    			}
	    		}
	    		if(res.exceptionInfo){
	    			showError(res.exceptionInfo);return;
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
    	var tb = Ext.getCmp('gl_info_ym');
    	if(tb)
    		tb.updateInfo(r);
    	return r;
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
		return Ext.create('erp.view.core.form.ConMonthDateField', {
			id: 'sl_yearmonth',
			name: 'sl_yearmonth',
			fieldLabel: '期间',
			labelWidth: 80,
			columnWidth: 1,
			getValue: function() {
				if(!Ext.isEmpty(this.value)) {
					return {begin: this.firstVal, end: this.secondVal};
				}
				return null;
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
			columnWidth: .51,
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
	createCateField : function() {
    	var me = this, t, t1;
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
    	return Ext.create('Ext.form.FieldContainer', {
    		fieldLabel: '科目编号',
    		labelWidth: 80,
			id: 'ca_code',
			hidden:true,
	    	columnWidth: 1,
	    	height: 56,
	    	layout: 'column',
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
    	});
    },
	createAssField : function() {
		var me = this ,t, t1;
		t = Ext.create('erp.view.core.trigger.DbfindTrigger', {
			id: 'ak_asscode',
			columnWidth: 0.4,
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
					var a = Ext.getCmp('ak_name'),
						n = Ext.getCmp('ak_assname');
					if(a.asskind) {
						f.setValue(r.data[a.asskind.ak_asscode]);
						n.setValue(r.data[a.asskind.ak_assname]);
					}
    			},
    			change: function(f) {
    				Ext.getCmp('ak_assname').setValue(null);
    			}
			}
		});
		t1 = Ext.create('erp.view.core.trigger.DbfindTrigger', {
			id: 'ak_asscode1',
			columnWidth: 0.4,
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
					var a = Ext.getCmp('ak_name'),
						n = Ext.getCmp('ak_assname1');
					if(a.asskind) {
						f.setValue(r.data[a.asskind.ak_asscode]);
						n.setValue(r.data[a.asskind.ak_assname]);
					}
    			},
    			change: function(f) {
    				Ext.getCmp('ak_assname1').setValue(null);
    			}
			}
		});
		return Ext.create('Ext.form.FieldContainer', {
			fieldLabel: '核算编号',
			labelWidth: 80,
			layout: 'column',
			columnWidth: 1,
			height: 56,
			id: 'ass_code',
			defaults: {
				fieldStyle : "background:#FFFAFA;color:#515151;"
			},
			items: [t, {
				xtype: 'textfield',
				name: 'ak_assname',
				id: 'ak_assname',
				columnWidth: 0.6,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			},t1,{
				xtype: 'textfield',
				name: 'ak_assname1',
				id: 'ak_assname1',
				columnWidth: 0.6,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			}],
			getValue: function() {
				var a = this.down('#ak_asscode').value,b = this.down('#ak_asscode1').value,
					x = Ext.isEmpty(a), y = Ext.isEmpty(b);
				if(x && y) {
					return null;
				}
				return {begin: a, end: b, current: this.current};
			}
		});
	},
    createFilterPanel: function(btn) {
    	var me = this;
    	var filter = Ext.create('Ext.Window', {
    		id: btn.getId() + '-filter',
    		style: 'background:#f1f1f1',
    		title: '筛选条件',
    		width: 500,
    		height: 425,
    	    layout: 'column',
    	    defaults: {
    	    	margin: '2 2 2 10'
    	    },
    	    items: [{
    	    	margin: '10 2 2 10',
    	    	xtype: 'dbfindtrigger',
				id: 'mas_name',
				name: 'mas_name',
				columnWidth: 0.8,
				fieldLabel: '多栏账名称',
				allowBlank: false,
				blankText : '请选择多栏账名称',
				labelWidth: 80,
				listeners: {
					aftertrigger: function(t, d) {
						t.setValue(d.data.mas_name);
						t.ownerCt.down('#ak_name').setValue(d.data.ak_name);
	    				var a = Ext.getCmp('ak_asscode'), a1 = Ext.getCmp('ak_asscode1'),
							b = Ext.getCmp('ak_assname'), b1 = Ext.getCmp('ak_assname1');
						a.setValue(null);
						b.setValue(null);
						a1.setValue(null);
						b1.setValue(null);
	    				if(Ext.isEmpty(t.ownerCt.down('#ak_name').value)) {
	    					t.ownerCt.down('#ak_name').asskind = null;
	    				}else{
	    					var dbfind = t.ownerCt.down('#ak_name');
	    					var which = 'form';
							var cal = dbfind.dbCaller||caller;
							var key = dbfind.triggerName||dbfind.name;
							var con = !Ext.isEmpty(dbfind.value) ? (key + " like '%" + dbfind.value.replace(/\'/g,"''")  + "%'") : null;
	    					dbfind.autoDbfind(which, cal, key, dbfind.getCondition(con));
						}
					}
				}
    	    },{
    	    	xtype:'numberfield',
    	    	id:'mas_id',
    	    	columnWidth: 0.8,
    	    	hidden:true
    	    },{
    	    	xtype:'numberfield',
    	    	id:'mas_assistant',
    	    	columnWidth: 0.8,
    	    	hidden:true
    	    },{
    	    	xtype:'button',
    	    	margin: '10 2 2 10',
    	    	text:'新增方案',
    	    	columnWidth: 0.2,
    	    	listeners:{
    	    		click:function(btn){
    	    			me.FormUtil.onAdd('addMulticolacScheme', '新增多栏账方案', 'jsps/fa/gla/multicolacScheme.jsp');
    	    		}
    	    	}
    	    },me.createYearmonthField(), me.createCurrencyField(),{
				xtype: 'checkbox',
				id: 'chkhaveun',
				name: 'chkhaveun',
				columnWidth: .5,
				boxLabel: '包含未记账凭证',
				checked: true
			},{
				xtype: 'checkbox',
				id: 'businessbranch',
				name: 'businessbranch',
				columnWidth: .5,
				boxLabel: '业务记录分行显示',
				checked: true
			},{
				xtype: 'checkbox',
				id: 'chkno',
				name: 'chkno',
				columnWidth: .5,
				boxLabel: '无发生额不显示'
			},{
				xtype: 'checkbox',
				id: 'monthend',
				name: 'monthend',
				columnWidth: .5,
				boxLabel: '显示明细项目期末余额',
				checked: true
			},{
				xtype: 'checkbox',
				id: 'chkdis',
				name: 'chkdis',
				columnWidth: .51,
				boxLabel: '显示禁用科目',
				checked: true
			},{
				xtype: 'dbfindtrigger',
				id: 'ak_name',
				name: 'ak_name',
				columnWidth: .51,
				labelWidth: 80,
				fieldLabel: '核算类型',
	    		listeners: {
	    			aftertrigger: function(f, r) {
	    				if(!Ext.isEmpty(f.value)) {
	    					f.asskind = r.data;
	    					var c1 = Ext.getCmp('ak_asscode'),
	    						n1 = Ext.getCmp('ak_assname'),
	    						c2 = Ext.getCmp('ak_asscode1'),
	    						n2 = Ext.getCmp('ak_assname1');
	    					c1.name = c2.name = r.data.ak_asscode;
	    					n1.name = n2.name = r.data.ak_assname;
	    				} else {
	    					f.asskind = null;
	    				}
	    			}
	    		}
			},me.createAssField(),{
				xtype: 'checkbox',
				id: 'chkall',
				name: 'chkall',
				hidden: true,
				columnWidth: .5,
				boxLabel: '显示辅助核算'
			},{
				xtype: 'checkbox',
				id: 'assbranch',
				name: 'assbranch',
				hidden: false,
				columnWidth: .5,
				boxLabel: '按核算项目分页显示',
				readOnly: true
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
			}],
			buttonAlign: 'center',
    	    buttons: [{
	    		text: '确定',
	    		width: 60,
	    		cls: 'x-btn-gray',
	    		handler: function(btn) {
    				var fl = btn.ownerCt.ownerCt, mas_name = fl.down('#mas_name'),
						con = me.getCondition(fl);
    				con.querytype = 'current';
    				if(Ext.isEmpty(mas_name.value)){
    					showError("请选择多栏账名称！");
    					return;
    				}
					me.query(con);
					fl.hide();
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