Ext.define('erp.view.scm.sale.batchDeal.MyForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpMyBatchDealFormPanel',
	requires: ['erp.view.core.button.VastDeal','erp.view.core.button.VastPrint','erp.view.core.button.LendTrimmer'],
	id: 'mydealform', 
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
	}, '->', {
		text:$I18N.common.button.erpLendTryButton,
		iconCls:'x-button-icon-submit',
		cls:'x-btn-gray',
		id:'lendtry',
		handler:function(btn){
			var me = this;
			var grid = Ext.getCmp('batchDealMyGridPanel');
			var items = grid.getMultiSelected();
			var sa_code = getUrlParam("sa_code1").split(",");
			var tqty = getUrlParam("tqty").split(",");
			var type = getUrlParam("type").split(",");
			var ob_id = getUrlParam("id").split(",");
			var pu_code = getUrlParam("pu_code").split(",");
			var pu_detno = getUrlParam("pu_detno1").split(",");
			var ob_sadetno = getUrlParam("ob_sadetno1").split(",");
			var qty = 0;
        	var aqty = 0;
        	for(var j=0;j<tqty.length;j++){
        		aqty = aqty+parseInt(tqty[j]);
        	}
        	for(var i=0;i<items.length;i++){
        		qty = qty+items[i].data.ob_tqty;
        	}
        	if(qty!=aqty){
        		showError("借调数量之和必须等于"+aqty);
        		return;
        	}
        	for(var i=0;i<items.length;i++){
        		if(items[i].data.sa_custcode!=items[0].data.sa_custcode){
        			showError("只能勾选相同的客户");
        			return;
        		}
        	}
        	var record = grid.selModel.selected.items;
			var jsonFormData = new Array();
			for(var j=0;j<type.length;j++){
				var a = new Object();
				a['ob_id']=ob_id[j];
				a['tqty']=tqty[j];
				a['type']=type[j];
				a['sa_code']=sa_code[j];
				a['ob_sadetno']=ob_sadetno[j];
				a['pu_code']=pu_code[j];
				a['pu_detno']=pu_detno[j];
				jsonFormData.push(Ext.JSON.encode(a));
			}
			var jsonGridData = new Array();
			for(var i=0;i<record.length;i++){
				var data = record[i].data;
			    var r=new Object();
			    r['ob_id']=data.ob_id;
			    r['type']=data.type;
			    r['ob_tqty']=data.ob_tqty; 
			    r['sa_code']=data.sa_code; 
			    r['sf_code']=data.sf_code; 
			    r['ob_sadetno']=data.ob_sadetno; 
			    r['sa_custcode']=data.sa_custcode; 
			    r['sa_custname']=data.sa_custname; 
			    r['pu_code']=data.pu_code; 
				r['pu_detno']=data.ob_pudetno; 
				r['pr_code']=data.pr_code; 
				r['sa_seller']=data.sa_seller; 
				r['sd_whname']=data.sd_whname; 
			    jsonGridData.push(Ext.JSON.encode(r));
			}
			var params=new Object();
			params.formdata = unescape(jsonFormData.toString().replace(/\\/g,"%"));
			params.data = unescape(jsonGridData.toString().replace(/\\/g,"%"));
			Ext.Ajax.request({
		   		url : basePath + 'scm/sale/addLendApply.action',
		   		params: {
		   			caller : caller,
		   			formdata: params.formdata,
		   			data:params.data
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var localJson = new Ext.decode(response.responseText);
	    			if(success){
	    				showMessage(localJson.log);
		   			} else if(localJson.exceptionInfo){
		   				var str = localJson.exceptionInfo;
		   				showError(str);
		   				} 
	        		}
			});
		}
	},{
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
	initComponent : function(){ 
    	this.getItemsAndButtons();
    	this.addEvents({alladded: true});//items加载完
		this.callParent(arguments);
		this.addKeyBoardEvents();//监听Ctrl+Alt+S事件
	},
	getItemsAndButtons: function(){
		var me = this;
		me.FormUtil.getActiveTab().setLoading(true);
		Ext.Ajax.request({//拿到form的items
        	url : basePath + 'common/singleFormItems.action',
        	params: {
        		caller: caller, 
        		condition: '',
        		_noc: getUrlParam('_noc') || this._noc,
        		_config:getUrlParam('_config')
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.FormUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		if(contains(res.buttons, 'addToTempStore', true)){
        			me.tempStore=true;
        		}
        		me.fo_keyField = res.fo_keyField;
        		me.detailkeyfield = res.detailkeyfield;
        		me.tablename = res.tablename;
        		me.fo_id=res.fo_id;
        		if(res.keyField){
        			me.keyField = res.keyField;
        		}
        		if(res.dealUrl){
        			me.dealUrl = res.dealUrl;
        		}
        		me.fo_detailMainKeyField = res.fo_detailMainKeyField;
        		Ext.each(res.items, function(item){
        			if(me.source=='allnavigation'){
        				item.fieldStyle = item.fieldStyle + ';background:#f1f1f1;';
						item.readOnly = true;
        			}
        			if(!item.allowBlank && item.fieldLabel ) {
        				item.fieldLabel= '<font style="color:#F00">'+item.fieldLabel+'</font>';
        			}
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
        		me.add(res.items);
        		me.fireEvent('alladded', me);
        		//解析buttons字符串，并拼成json格式
        		var buttonString = res.buttons;
        		if(buttonString != null && buttonString != ''){
        			if(contains(buttonString, '#', true)){
        				Ext.each(buttonString.split('#'), function(b, index){
        					if(!Ext.getCmp(b)){
        						var btn = Ext.getCmp('erpVastDealButton');
        						if (btn){
        							try {
        								btn.ownerCt.insert(5, {
            								xtype: b,
            								disabled:me.source=='allnavigation'?true:false,
            								cls: 'x-btn-gray'
            							});
        							} catch (e) {
        								btn.setText($I18N.common.button[b]);
                        				btn.show();
        							}
        						}
        					} else {
        						Ext.getCmp(b).show();
        						if(me.source=='allnavigation') Ext.getCmp(buttonString).setDisabled(true);
        					}
        				});
        			} else {
        				if(caller == 'NewBar!BaPrint' || caller == 'Barcode!BaPrint'){
        					var btn = Ext.getCmp('erpVastDealButton');
        					if (btn){
        						try {
        							btn.ownerCt.insert(2, {
            							xtype: buttonString,
            							disabled:me.source=='allnavigation'?true:false,
            							cls: 'x-btn-gray'
            						});
        						} catch (e) {
        								btn.setText($I18N.common.button[buttonString]);
                        				btn.show();
        							}
        						}
        				}else{
	        				if(Ext.getCmp(buttonString)){
	        					Ext.getCmp(buttonString).show();
	        					if(me.source=='allnavigation') Ext.getCmp(buttonString).setDisabled(true);
	        				} else {
	        					var btn = Ext.getCmp('erpVastDealButton');//Ext.getCmp(buttonString);
	                			if(btn){
	                				btn.setText($I18N.common.button[buttonString]);
	                				btn.show();
	                				if(me.source=='allnavigation') btn.setDisabled(true);
	                			}
	        				}
        				}
        			}
        		}
        	}
        });
	},
	/**
	 * @param select 保留原筛选行
	 */
	onQuery: function(select){
		var grid = Ext.getCmp('batchDealMyGridPanel'),sel = [];
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
		} else {
			//grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
			grid.GridUtil.loadNewStore(grid, gridParam);
		}
		if(select == true) {
			Ext.each(sel, function(){
				grid.selModel.select(this.index);
			});
		}
	},
	getCondition: function(grid){
		grid = grid || Ext.getCmp('batchDealMyGridPanel');
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
	/**
	 * 监听一些事件
	 * <br>
	 * Ctrl+Alt+S	单据配置维护
	 * Ctrl+Alt+P	参数、逻辑配置维护
	 */
	addKeyBoardEvents: function(){
		var me = this;
		Ext.EventManager.addListener(document.body, 'keydown', function(e){
			if(e.altKey && e.ctrlKey) {
				if(e.keyCode == Ext.EventObject.S) {
					var url = "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + "&gridCondition=fd_foidIS" + me.fo_id, 
						forms = Ext.ComponentQuery.query('form'), 
						grids = Ext.ComponentQuery.query('gridpanel'),
						formSet = [], gridSet = [];
					if(forms.length > 0) {
						Ext.Array.each(forms, function(f){
							f.fo_id && (formSet.push(f.fo_id));
						});
					}
					if(grids.length > 0) {
						Ext.Array.each(grids, function(g){
							if(g.xtype.indexOf('erpMyBatchDealGridPanel') > -1){
								gridSet.push(window.caller);
							}else if(g.caller){
								gridSet.push(g.caller);
							}
						});
					}
					if(formSet.length > 0 || gridSet.length > 0) {
						url = "jsps/ma/multiform.jsp?formParam=" + formSet.join(',') + '&gridParam=' + gridSet.join(',');
					}
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', url);
				} else if(e.keyCode == Ext.EventObject.P) {
					me.FormUtil.onAdd('configs-' + caller, '逻辑配置维护(' + caller + ')', "jsps/ma/logic/config.jsp?whoami=" + caller);
				}
			}
		});
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