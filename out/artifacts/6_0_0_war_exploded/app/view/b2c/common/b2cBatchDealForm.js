Ext.define('erp.view.b2c.common.b2cBatchDealForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpBatchDealFormPanel',
	requires: ['erp.view.core.button.VastDeal','erp.view.core.button.TurnGoodsUp',
	           'erp.view.core.button.VastPrint','erp.view.core.button.B2CPurchase',
	           'erp.view.core.button.BatchQuotePrice'
	           ],
	id: 'dealform', 
    region: 'north',
    tempStore:false,
    detailkeyfield:'',
    frame : true,
    header: false,//不显示title
	layout : 'column',
	padding: '0 4 0 4',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	formitems:[],
	fbuttons:[],
	condition:'',
	fieldDefaults : {
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	GridUtil: Ext.create('erp.util.GridUtil'),
	tbar: [{
		name: 'query',
		id: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	handler: function(btn){
			btn.ownerCt.ownerCt.onQuery();
    	}
	},{
	  id:'reminder',
	  style:'background:red!important',
	  xtype:'tbtext',
	  hidden:true
	},
    '->', 
	{
		name: 'addToTempStore',
		id: 'addToTempStore',
		text: $I18N.common.button.erpAddToTempStore,
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	hidden: true
    },{
		name: 'checkTempStore',
		id: 'checkTempStore',
		hidden: true,
		text: $I18N.common.button.erpCheckTempStore,
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray'
    },'-',{
    	xtype:'erpBatchPriceButton',
    	id:'erpBatchPriceButton',
    	hidden:true
    },{
    	xtype: 'erpVastPrintButton',
    	id: 'erpVastPrintButton',
    	hidden: true
    },{
    	xtype: 'erpVastDealButton',
    	id: 'erpVastDealButton',
    	hidden: true
    },{
    	id:'import',
		name: 'import',
		text: '选 择',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	hidden:caller == 'BatchUUIdSource' ? false:true
	},'-',{
    	name: 'export',
    	id:'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-excel',
    	cls: 'x-btn-gray',
    	handler: function(btn){
    		var form = btn.ownerCt.ownerCt;
    		var grid = form.ownerCt.down('grid');
    		var cond = form.getCondition();
    		if(Ext.isEmpty(cond)) {
    			cond = '1=1';
    		}
    		if(grid.xtype == 'erpBatchDealGridPanel') {
    			var p = grid.plugins[1], fields = Ext.Object.getKeys(p.fields),
    				fi = new Array();
    			fi.push(cond);
    			Ext.each(fields, function(){
    				var f = p.fields[this];
    				if(!Ext.isEmpty(f.value)) {
    					if((f.xtype == 'datefield' || f.xtype == 'datetimefield')
    						&& f.value instanceof Date) {
    						fi.push('to_char(' + this + ',\'yyyymmdd\')=' + Ext.Date.format(f.value, 'Ymd'));
    					} else {
    						fi.push(this + ' like \'%' + f.value + '%\'');
    					}
    				}
    			});
    			cond = fi.join(' AND ');
    		}
    		grid.BaseUtil.createExcel(caller, 'detailgrid', cond);
    	}
    },'-',{
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	id:'close',
    	handler: function(btn){
    		var main = parent.Ext.getCmp("content-panel"); 
    		if(main){
    			main.getActiveTab().close();
    		}else parent.Ext.getCmp('win').close();
    	}
	}],
	initComponent : function(){  
		this.callParent(arguments);
		this.getItemsAndButtons();
	},
	getItemsAndButtons: function(){
		var me = this,form = this;
		form.buttons = form.fbuttons;
		if(contains(form.buttons, 'addToTempStore', true)){
			me.tempStore=true;
		}
		Ext.each(form.formitems, function(item){
			if(screen.width < 1280){//根据屏幕宽度，调整列显示宽度
				if(item.columnWidth > 0 && item.columnWidth <= 0.25){
					item.columnWidth = 1/3;
				} else if(item.columnWidth > 0.25 && item.columnWidth <= 0.5){
					item.columnWidth = 2/3;
				} else if(item.columnWidth >= 1){
					item.columnWidth = 1;
				}
			} else {
				if(item.columnWidth > 0.25 && item.columnWidth < 0.5){
					item.columnWidth = 2/3;
				}
			}
			if(item.xtype == 'hidden') {
				item.columnWidth = 0;
			}
		});
		me.add(form.formitems);
		me.fireEvent('alladded', me);
        //解析buttons字符串，并拼成json格式
		var buttonString = form.buttons;
		if(buttonString != null && buttonString != ''){
			if(contains(buttonString, '#', true)){
				Ext.each(buttonString.split('#'), function(b, index){
					if(!Ext.getCmp(b)){
						var btn = Ext.getCmp('erpVastDealButton');
						if (btn){
							try {
								btn.ownerCt.insert(5, {
    								xtype: b,
    								cls: 'x-btn-gray'
    							});
							} catch (e) {
								btn.setText($I18N.common.button[b]);
                				btn.show();
							}
						}
					} else {
						Ext.getCmp(b).show();
					}
				});
			} else {
				if(Ext.getCmp(buttonString)){
					Ext.getCmp(buttonString).show();
				} else {
					var btn = Ext.getCmp('erpVastDealButton');//Ext.getCmp(buttonString);
        			if(btn){
        				btn.setText($I18N.common.button[buttonString]);
        				btn.show();
        			}
				}
			}
		}
	},
	/**
	 * @param select 保留原筛选行
	 */
	onQuery: function(select){
		var grid = Ext.getCmp('batchDealGridPanel'),sel = [];
		var check=grid.headerCt.items.items[0];
		if(check && check.isCheckerHd){
			check.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
		}
		grid.multiselected = new Array();
		if(select == true) {
			sel = grid.selModel.getSelection();
		}
		var form = this;
		var cond = form.getCondition();
		if(Ext.isEmpty(cond)) {
			cond = '1=1';
		}
		var constr=form.beforeQuery(caller, cond);//执行查询前逻辑
		cond+=constr!=null && constr!=''?" AND ("+constr+")":"";
		if(caller == 'MRPOnhandThrow'){
			cond+=" and mdd_action='UP' and mdd_status='未投放'";
		}
		if(form.defaultCondition){
			cond+=" and"+form.defaultCondition;
		}
		cond+=" and rownum<1000"
		//+ form.getOrderBy(grid)
		var gridParam = { caller: caller, condition: cond,fields:grid.datafields,tablename:grid.tablename};
		/*if(!grid.bigVolume) {
			gridParam.start = 1;
			gridParam.end = 1000;
		}
		if(grid.maxDataSize) {
			gridParam.start = 1;
			gridParam.end = grid.maxDataSize;
		}*/
		//移除掉全选样式
		//grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
		grid.loadNewStore(grid, gridParam);
		if(select == true) {
			Ext.each(sel, function(){
				grid.selModel.select(this.index);
			});
		}
		if(caller=='QuotePrice'){
			var rate = Ext.getCmp("monthrate").value;
			Ext.each(grid.store.data.items,function(item){
				item.set("monthrate",rate);	
			});
		}
	},
	getCondition: function(grid){
		grid = grid || Ext.getCmp('batchDealGridPanel');
		if(!grid){
			grid = Ext.getCmp('grid');
		}
		var form = this;
		var condition = typeof grid.getCondition === 'function' ? grid.getCondition(true) : 
			(Ext.isEmpty(grid.defaultCondition) ? '' : ('(' + grid.defaultCondition + ')'));
		Ext.each(form.items.items, function(f){
			if(f.logic != null && f.logic != ''){
				if((f.xtype == 'checkbox' || f.xtype == 'radio')){
					if(f.value == true) {
						if(condition == ''){
							condition += f.logic;
						} else {
							condition += ' AND ' + f.logic;
						}
					}
				} else if(f.xtype == 'datefield' && f.value != null && f.value != '' && !contains(f.logic, 'to:', true)){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d');
					if(condition == ''){
						condition += "to_char("+f.logic+",'yyyy-MM-dd')='"+v+"'";
					} else {
						condition += " AND to_char("+f.logic+",'yyyy-MM-dd')='"+v+"'";
					}
				} else if(f.xtype == 'datetimefield' && f.value != null){
					
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d H:i:s');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					}
				} else if(f.xtype == 'numberfield' && f.value != null && f.value != '' && !contains(f.logic, 'to:', true)){
					var endChar = f.logic.substr(f.logic.length - 1);
					if(endChar != '>' && endChar != '<')
						endChar = '=';
					else
						endChar = '';
					if(condition == ''){
						condition += f.logic + endChar + f.value;
					} else {
						condition += ' AND ' + f.logic + endChar + f.value;
					}
				} else if(f.xtype == 'combo' && f.value == '$ALL'){
					if(f.store.data.length > 1) {
						if(condition == ''){
							condition += '(';
						} else {
							condition += ' AND (';
						}
						var _a = '';
						f.store.each(function(d, idx){
							if(d.data.value != '$ALL') {
								if(_a == ''){
									_a += f.logic + "='" + d.data.value + "'";
								} else {
									_a += ' OR ' + f.logic + "='" + d.data.value + "'";
								}
							}
						});
						condition += _a + ')';
					}
				} else if(f.xtype=='adddbfindtrigger' && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + ' in (' ;
					}else{
						condition += ' AND ' + f.logic + ' in (';
					}
					var str=f.value,constr="";
					for(var i=0;i<str.split("#").length;i++){
						if(i<str.split("#").length-1){
							constr+="'"+str.split("#")[i]+"',";
						}else constr+="'"+str.split("#")[i]+"'";
					}
					condition +=constr+")";
				} else {
					//一般情况下，在执行批量处理时,是不需要把form的数据传回去,
					//但某些情况下，需要将form的某些字段的值也传回去
					//例如 请购批量转采购，如果指定了采购单号，就要把采购单号传回去
					if(contains(f.logic, 'to:', true)){
						if(!grid.toField){
							grid.toField = new Array();
						}
						grid.toField.push(f.logic.split(':')[1]);
					} else {
						if(!Ext.isEmpty(f.value)){
							if(contains(f.value.toString(), 'BETWEEN', true) && contains(f.value.toString(), 'AND', true)){
								if(condition == ''){
									condition += f.logic + " " + f.value;
								} else {
									condition += ' AND (' + f.logic + " " + f.value + ")";
								}
							} else if(contains(f.value.toString(), '||', true)){
								var str = '';
								Ext.each(f.value.split('||'), function(v){
									if(v != null && v != ''){
										if(str == ''){
											str += f.logic + "='" + v + "'";
										} else {
											str += ' OR ' + f.logic + "='" + v + "'";
										}
									}
								});
								if(condition == ''){
									condition += "(" + str + ")";
								} else {
									condition += ' AND (' + str + ")";
								}
							} else if(f.value.toString().charAt(0) == '!'){ 
								if(condition == ''){
									condition += 'nvl(' + f.logic + ",' ')<>'" + f.value.substr(1) + "'";
								} else {
									condition += ' AND (nvl(' + f.logic + ",' ')<>'" + f.value.substr(1) + "')";
								}
							} else {
								if(f.value.toString().indexOf('%') >= 0) {
									if(condition == ''){
										condition += f.logic + " like '" + f.value + "'";
									} else {
										condition += ' AND (' + f.logic + " like '" + f.value + "')";
									}
								} else {
									if(condition == ''){
										condition += f.logic + "='" + f.value + "'";
									} else {
										condition += ' AND (' + f.logic + "='" + f.value + "')";
									}
								}
							}
						}
					}
				}
			}
		});
		if(form.condition != null && form.condition != ''){
			condition =condition+" and "+form.condition;
		}
		return condition;
	},
	getOrderBy: function(grid){
		var ob = new Array();
		if(grid.mainField) {
			ob.push(grid.mainField + ' desc');
		}
		if(grid.detno) {
			ob.push(grid.detno + ' asc');
		}
		if(grid.keyField) {
			ob.push(grid.keyField + ' desc');
		}
		var order = '';
		if(ob.length > 0) {
			order = ' order by ' + ob.join(',');
		}
		return order;
	},
	beforeQuery: function(call, cond) {
		var str=null;
		Ext.Ajax.request({
			url: basePath + 'common/form/beforeQuery.action',
			params: {
				caller: call,
				condition: cond
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				}else if(rs.data){
					str=rs.data;
				}
			}
		});
		return str;
	}
});