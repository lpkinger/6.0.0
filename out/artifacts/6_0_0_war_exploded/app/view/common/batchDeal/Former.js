Ext.define('erp.view.common.batchDeal.Former',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpBatchDealerFormPanel',
	requires: ['erp.view.core.button.VastDeal','erp.view.core.button.VastPrint','erp.view.core.button.VastAnalyse','erp.view.core.button.TurnVastOtherIn',
	           'erp.view.core.button.GetVendor','erp.view.core.button.VastTurnPurc','erp.view.core.button.DealMake','erp.view.core.button.TurnVastSaleReturn',
	           'erp.view.core.button.MakeOccur','erp.view.core.button.SaleOccur','erp.view.core.button.AllThrow','erp.view.core.button.AllThrowNotify',
	           'erp.view.core.button.SelectThrow','erp.view.core.button.GetPrice','erp.view.core.button.RePrice','erp.view.core.button.SelectThrowNotify',
	           'erp.view.core.button.BussAccount','erp.view.core.button.FeeShare','erp.view.core.button.VastSave','erp.view.core.button.TurnStockScrap',
	           'erp.view.core.button.UseableReplace','erp.view.core.button.Consistency', 'erp.view.core.button.VastPost','erp.view.core.button.BatchTurnAppropriationOut',
	           'erp.view.core.button.VastTurnPreProduct', 'erp.view.core.button.RefreshQty', 'erp.view.core.button.SetVendorRate','erp.view.core.button.CancelApproveNum',
	           'erp.view.core.button.ScmTurnOtherOut','erp.view.core.button.ScmTurnExchangeOut','erp.view.core.button.ScmTurnAppropriationOut','erp.view.core.button.TurnGoodsUp',
	           'erp.view.core.button.ConfirmVendor','erp.view.core.button.GetForecastVendor','erp.view.core.button.VastTurnProdIn','erp.view.core.button.SetVendorRateAdd',
	           'erp.view.core.button.DeliveryChange','erp.view.core.button.SaveCostDetail','erp.view.core.button.DifferVoucherCredit','erp.view.core.button.NowhVoucherCredit',
	           'erp.view.core.button.BatchPrint','erp.view.core.button.B2CPurchase','erp.view.core.button.ConfirmYFYF','erp.view.core.button.ConfirmYSYS','erp.view.core.button.VastMakeOpen',
	           'erp.view.core.button.VastMakeClose','erp.view.core.button.CallBack','erp.view.core.button.Transfer','erp.view.core.button.BusDistribute','erp.view.core.button.BatchQuotePrice','erp.view.core.button.ReleaseBarcode',
	           'erp.view.core.button.VastCancelSubs','erp.view.core.button.VastAddSubs','erp.view.core.button.OpenVendorUU','erp.view.core.button.CancelVendorUU','erp.view.core.button.OpenB2BDelivery','erp.view.core.button.FreezeBarcode',
	           'erp.view.core.button.CancelB2BDelivery','erp.view.core.button.OpenB2BCheck','erp.view.core.button.CancelB2BCheck','erp.view.core.button.ChargerCalc','erp.view.core.button.CheckVendorUU','erp.view.core.button.AddLocked',
	           'erp.view.core.button.HandLocked','erp.view.core.button.LendTrim','erp.view.core.button.LendTry','erp.view.core.button.LendTrimmer','erp.view.core.button.SubsBatchTest'
	           ,'erp.view.core.button.VastUnPowerProdSaler'
	           ,'erp.view.core.button.VastEmpowerProdSaler'],
	id: 'dealerform', 
	source:'',//全功能导航展示使用
    region: 'north',
    tempStore:false,
    detailkeyfield:'',
    frame : true,
    header: false,//不显示title
	layout : 'column',
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
	tbar: {defaults:{height:26,margin:'0 5 5 0'},items:[{
		name: 'query',
		id: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	handler: function(btn){
			btn.ownerCt.ownerCt.onQuery();
    	}
	},{
    	xtype: 'erpVastEmpowerProdSalerButton',
    	id: 'erpVastEmpowerProdSalerButton',
    	hidden: true
    },{
    	xtype: 'erpVastUnPowerProdSalerButton',
    	id: 'erpVastUnPowerProdSalerButton',
    	hidden: true
    },{
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
    },{
    	xtype: 'erpMakeOccurButton',
    	id: 'erpMakeOccurButton',
    	hidden: true
    },
    {
    	xtype: 'erpSaleOccurButton',
    	id: 'erpSaleOccurButton',
    	hidden: true
    },
    {
    	xtype: 'erpVastAnalyseButton',
    	id: 'erpVastAnalyseButton',
    	hidden: true
    },{
    	xtype: 'erpVastPrintButton',
    	id: 'erpVastPrintButton',
    	hidden: true
    },{
    	xtype: 'erpVastDealButton',
    	id: 'erpVastDealButton',
    	hidden: true
    },
    {
    	xtype:'erpBatchPriceButton',
    	id:'erpBatchPriceButton',
    	hidden:true
    }
    ,{
    	xtype: 'erpRefreshQtyButton',
    	id: 'erpRefreshQtyButton',
    	hidden: true
    },{
    	xtype: 'erpDeliveryChangeButton',
    	id: 'erpDeliveryChangeButton',
    	hidden: true
    },{
    	xtype: 'erpSaveCostDetailButton',
    	id: 'erpSaveCostDetailButton',
    	hidden: true
    },{
    	xtype: 'erpDifferVoucherCreditButton',
    	id: 'erpDifferVoucherCreditButton',
    	hidden: true
    },{
    	xtype: 'erpNowhVoucherCreditButton',
    	id: 'erpNowhVoucherCreditButton',
    	hidden: true
    },{
    	xtype: 'erpConfirmYFYFButton',
    	hidden: true
    },{
    	xtype: 'erpConfirmYSYSButton',
    	hidden: true
    },{
    	xtype: 'erpVastAddSubsApplyButton',
    	id: 'erpVastAddSubsApplyButton',
    	hidden: true
    },{
    	xtype: 'erpVastCancelSubsApplyButton',
    	id: 'erpVastCancelSubsApplyButton',
    	hidden: true
    },{
    	xtype: 'erpOpenVendorUUButton',
    	id: 'erpOpenVendorUUButton',
    	hidden: true
    },{
    	xtype: 'erpCheckVendorUUButton',
    	id: 'erpCheckVendorUUButton',
    	hidden: true
    },{
    	xtype: 'erpAddLocked',
    	id: 'erpAddLockedButton',
    	hidden: true
    },{
    	xtype: 'erpLendTry',
    	id: 'erpLendTryButton',
    	hidden: true
    },{
    	xtype: 'erpLendTrimmer',
    	id: 'erpLendTrimmerButton',
    	hidden: true
    },{
    	xtype: 'erpHandLocked',
    	id: 'erpHandLockedButton',
    	hidden: true
    },{
    	xtype: 'erpLendTrim',
    	id: 'erpLendTrimButton',
    	hidden: true
    },{
    	xtype: 'erpChargerCalcButton',
    	id: 'erpChargerCalcButton',
    	hidden: true
    },{
    	xtype: 'erpSubsBatchTestButton',
    	id: 'erpSubsBatchTestButton',
    	hidden: true
    },{
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
    		if(grid.xtype == 'erpBatchDealerGridPanel') {
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
    },'->',{
    	margin:'0',
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
	}]},
	initComponent : function(){ 
		var source=getUrlParam('source');
		this.source=source;
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
        			if(item.xtype=='checkbox'){
        				item.margin = '7 0 3 80';
        				if(item.columnWidth<0.25){
        					item.margin = '7 0 3 0';
        				}
        			}else{
        				item.labelAlign = 'right';
        				item.fieldStyle = 'background:#ffffff;color:#515151;';
        			}
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
        			if(!item.allowBlank&&item.fieldLabel){ 
        				var text = item.fieldLabel;
        				if(item.fieldLabel){	
        					text = item.fieldLabel.substring(text.indexOf('">')+2,text.indexOf('</'));
        				}
        				item.fieldLabel = "<font color=\"red\"  style=\"position:relative; top:2px;right:2px;font-weight: bolder;\">*</font>"+text;
        				item.labelStyle = "color:#515151";
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
		var grid = Ext.getCmp('batchDealerGridPanel'),sel = [];
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
		var gridParam = { caller: caller, condition: cond + form.getOrderBy(grid),page:page,pageSize:pageSize };
		//移除掉全选样式
		grid.getGridColumnsAndStore(grid, 'common/batchdeal/data.action',gridParam);
		if(select == true) {
			Ext.each(sel, function(){
				grid.selModel.select(this.index);
			});
		}
	},
	getCondition: function(grid){
		grid = grid || Ext.getCmp('batchDealerGridPanel');
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
										condition += '('+f.logic + "='" + f.value + "')";
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
							if(g.xtype.indexOf('erpBatchDealerGridPanel') > -1)
								gridSet.push(window.caller);
							else if(g.caller)
								gridSet.push(g.caller);
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