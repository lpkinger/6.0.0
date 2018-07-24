Ext.define('erp.view.vendbarcode.batchDelivery.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpBatchDeliveryFormPanel',
	requires: ['erp.view.core.button.VastDeal','erp.view.core.button.VendVastTurnPurchase'
	           ],
	id: 'dealform', 
	source:'',//全功能导航展示使用
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
	BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
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
	}, '->',
	{
    	xtype: 'erpVendVastTurnPurchaseButton',
    	id: 'erpVendVastTurnPurchaseButton',
    },'-',{
    	name: 'export',
    	id:'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-excel',
    	cls: 'x-btn-gray',
    	handler: function(btn){
    		var form = btn.ownerCt.ownerCt;
    		//	grid = Ext.getCmp('batchDealGridPanel');
    		var grid = form.ownerCt.down('grid');
    		var cond = form.getCondition();
    		if(Ext.isEmpty(cond)) {
    			cond = '1=1';
    		}
    		if(grid.xtype == 'erpBatchDeliveryGridPanel') {
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
    		grid.BaseUtil.exportGrid(grid,grid.title);
    	}
    },'-',{
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	id:'close',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		if(main){
    		main.getActiveTab().close();
    		}else parent.Ext.getCmp('win').close();
    	}
	}],
	items : [{
		xtype: 'multidbfindtrigger',
		triggerCls: 'x-form-search-trigger',
		fieldLabel:"采购单号",
		labelWidth:70,
		columnWidth:0.5,
		name:'pu_code',
		id:'pu_code',
		margin:'5 0 0 0',
		logic:'pu_code',
		queryMode: 'local',
	},{
		xtype: 'multidbfindtrigger',
		triggerCls: 'x-form-search-trigger',
		fieldLabel:"物料编号",
		labelWidth:70,
		columnWidth:0.5,
		name:'pr_code',
		id:'pr_code',
		margin:'5 0 0 0',
		logic:'pr_code',
		queryMode: 'local',
	},{
		xtype: 'condatefield',
		fieldLabel:"采购日期",
		value:7,
		labelWidth:70,
		columnWidth:0.5,
		name:'pu_date',
		id:'pu_date',
		margin:'5 0 0 0',
		logic:'pu_date'
	},
	{
		xtype: 'condatefield',
		fieldLabel:"交货日期",
		labelWidth:70,
		value:7,
		columnWidth:0.5,
		name:'pu_delivery',
		id:'pu_delivery',
		margin:'5 0 0 0',
		logic:'pu_delivery'
	},
	{
		xtype: 'textfield',
		fieldLabel:"品牌",
		labelWidth:70,
		columnWidth:0.25,
		name:'pr_brand',
		id:'pr_brand',
		margin:'5 0 0 0',
		logic:'pr_brand',
		queryMode: 'local',
	},
	{xtype: 'textfield',
		fieldLabel:"原厂型号",
		labelWidth:70,
		columnWidth:0.25,
		name:'pr_orispeccode',
		id:'pr_orispeccode',
		margin:'5 0 0 0',
		logic:'pr_orispeccode',
		queryMode: 'local',
	}],
	initComponent : function(){ 
		var me = this;
    	/*this.getItemsAndButtons();*/
    	this.addEvents({alladded: true});//items加载完
		this.callParent(arguments);
	},
	/**
	 * @param select 保留原筛选行
	 */
	onQuery: function(select){
		var grid = Ext.getCmp('batchDeliveryGridPanel'),sel = [];
		if(!grid){
			grid = Ext.getCmp('grid');
		}
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
		var gridParam = { caller: caller, condition: cond + form.getOrderBy(grid) };
		if(!grid.bigVolume) {
			gridParam.start = 1;
			gridParam.end = 1000;
		}
		if(grid.maxDataSize) {
			gridParam.start = 1;
			gridParam.end = grid.maxDataSize;
		}
		//移除掉全选样式
		if(grid.getGridColumnsAndStore){
			grid.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
			Ext.each(grid.columns,function(col,index){
				if(col.dataIndex=='tr_paydate')
				col.renderer=function(val, meta, record, x, y, store, view){
					if(val){
						var flag=form.compareTime(val);
						date = Ext.Date.format(val, 'Y-m-d');
						return flag?'<span style="color:red;padding-left:2px;">' + date + '</span>':date;
					}
				};
			});
		} else {
			this.loadNewStore(grid, gridParam);
			/*grid.GridUtil.loadNewStore(grid, gridParam);*/
		}
		if(select == true) {
			Ext.each(sel, function(){
				grid.selModel.select(this.index,true,true);
			});
		}
	},
	compareTime:function(paydate){
		var now = new Date(); 
		var nowTime=now.getTime();
	   	var payms = Date.parse(new Date(paydate));
		if(payms-nowTime>0){
			return true;
		}else{
			return false;
		}
	},
	getCondition: function(grid){
		grid = grid || Ext.getCmp('batchDeliveryGridPanel');
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
							condition += "("+f.logic+")";
						} else {
							condition += ' AND (' + f.logic+')';
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
				} else if((f.xtype=='adddbfindtrigger' || f.xtype=='multidbfindtrigger') && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + ' in (' ;		
					} else {
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
		/*if(urlcondition !=null || urlcondition !=''){
			condition =condition+urlcondition; 
		}*/
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
	},
	/**
	 * 按钮宽度
	 */
	getButtonTextLength: function(s) {
		for (var l = s.length, c = 0, i = 0; i < l; i++)
			s.charCodeAt(i) < 27 || s.charCodeAt(i) > 126 ? c += 14 : c += 10;
		return c + 20;
	},
	loadNewStore: function(grid,param){
		var me = this;
		grid.setLoading(true);//loading...
		if(!param._config) param._config=getUrlParam('_config');
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "vendbarcode/batch/getPurchaseData.action",
        	params: param,
        	method : 'post',
        	callback : function(options,success,response){
        		grid.setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.data;
        		if(!data || data.length == 0){
        			grid.store.removeAll();
        			me.add10EmptyItems(grid);
        		} else {
        			if(grid.buffered) {
        				var ln = data.length, records = [], i = 0;
        			    for (; i < ln; i++) {
        			        records.push(Ext.create(grid.store.model.getName(), data[i]));
        			    }
        			    grid.store.purgeRecords();
        			    grid.store.cacheRecords(records);
        			    grid.store.totalCount = ln;
        			    grid.store.guaranteedStart = -1;
        			    grid.store.guaranteedEnd = -1;
        			    var a = grid.store.pageSize - 1;
        			    a = a > ln - 1 ? ln - 1 : a;
        			    grid.store.guaranteeRange(0, a);
        			} else {
        				grid.store.loadData(data);
        			}
        		}
        		//自定义event
        		grid.addEvents({
        		    storeloaded: true
        		});
        		grid.fireEvent('storeloaded', grid, data);
        	}
        });
	},
	add10EmptyItems: function(grid){
		var items = grid.store.data.items;
		var detno = grid.detno;
		var formCondition1 = this.BaseUtil.getUrlParam('formCondition');
		var formconfig = (formCondition1 == null) ? ['',''] : formCondition1.replace(/IS/g,"=").split('=');
		var mta_keyid = formconfig[1]=='' ? 0 :formconfig[1];
		if(detno){
			var index = items.length == 0 ? 0 : Number(items[items.length-1].data[detno]);
			for(var i=0;i<10;i++){
				var o = new Object();
				o[detno] = index + i + 1;
				o['mta_caller']=caller;
				o['mta_keyid']=mta_keyid;
				grid.store.insert(items.length, o);
				items[items.length-1]['index'] = items.length-1;
			}
		} else {
			for(var i=0;i<10;i++){
				var o = new Object();
				grid.store.insert(items.length, o);
				o['mta_caller']=caller;
				o['mta_keyid']=mta_keyid;
				items[items.length-1]['index'] = items.length-1;
			}
		}
		
	}
});