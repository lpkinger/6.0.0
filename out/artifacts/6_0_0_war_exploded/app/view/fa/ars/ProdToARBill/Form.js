Ext.define('erp.view.fa.ars.ProdToARBill.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpProdToARBillFormPanel',
	id: 'dealform', 
    region: 'north',
    frame : true,
    header: false,//不显示title
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#fff;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	tbar: {defaults:{margin:'0 5 0 0'},items:[{
		name: 'query',
		id: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	handler: function(){
			Ext.getCmp('dealform').onQuery();
    	}
	},  {
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
    	xtype: 'erpByAmountButton',
    	id: 'erpByAmountButton',
    	hidden: true
    },{
    	xtype: 'erpVastDealButton',
    	id: 'erpVastDealButton',
    	hidden: true
    },{
    	xtype: 'erpVastTurnARAPCheckButton',
    	id: 'erpVastTurnARAPCheckButton',
    	hidden: true
    },{
    	name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-excel',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var grid = Ext.getCmp('batchDealGridPanel');
    		grid.BaseUtil.exportGrid(grid);
    	}
    },'->',{
    	margin:'0',
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}]},
	initComponent : function(){ 
    	this.getItemsAndButtons();
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
        		condition: ''
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.FormUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		me.fo_keyField = res.fo_keyField;
        		me.tablename = res.tablename;
        		me.fo_id=res.fo_id;
        		if(res.keyField){
        			me.keyField = res.keyField;
        		}
        		if(res.dealUrl){
        			me.dealUrl = res.dealUrl;
        		}
        		me.fo_detailMainKeyField = res.fo_detailMainKeyField;
        		me.fo_detailGridOrderBy = res.fo_detailGridOrderBy;
        		Ext.each(res.items, function(item){
        			item.labelAlign = 'right';
    				item.fieldStyle = 'background:#FFF;color:#515151;';
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
        						if(btn){
        							btn.ownerCt.insert(2, {
            							xtype: b
            						});
            						Ext.getCmp(b).show();
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
        	}
        });
	},
	onQuery: function(){
		var grid = Ext.getCmp('batchDealGridPanel');
		var check=grid.headerCt.items.items[0];
		if(check && check.isCheckerHd){
			check.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
		}
		grid.multiselected = new Array();
		var form = this;
	    var ab_date_str = Ext.getCmp('ab_date')?(Ext.getCmp('ab_date').rawValue!=''?Ext.getCmp('ab_date').rawValue:null):null;	    
	    var condition = (grid.defaultCondition && grid.defaultCondition!=''&&grid.defaultCondition!=null)?' 1=1 and '+grid.defaultCondition:' 1=1 ';
		Ext.each(form.items.items, function(f){
			if(f.logic != null && f.logic != ''){
				//一般情况下，在执行批量处理时,是不需要把form的数据传回去,
				//但某些情况下，需要将form的某些字段的值也传回去
				//例如 请购批量转采购，如果指定了采购单号，就要把采购单号传回去
				if(contains(f.logic, 'to:', true)){
					if(!grid.toField){
						grid.toField = new Array();
					}
					grid.toField.push(f.logic.split(':')[1]);
				} else {
					if(f.xtype == 'checkbox'  || f.xtype == 'radio'){
						if(condition == ''){
							condition += f.logic;
						} else {
							condition += ' AND ' + f.logic;
						}
					} else if(f.xtype == 'datefield' && f.value != null&&!contains(f.logic, 'to:', true)){
						var v = Ext.Date.format(new Date(f.value), 'Y-m-d');
						if(condition == ''){
							condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
						} else {
							condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
						}
					} else if(f.xtype == 'datetimefield' && f.value != null){
						var v = Ext.Date.format(new Date(f.value), 'Y-m-d H:i:s');
						if(condition == ''){
							condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
						} else {
							condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
						}
					} else if(f.xtype == 'numberfield' && f.value != null && f.value != ''){
						if(condition == ''){
							condition += f.logic + '=' + f.value;
						} else {
							condition += ' AND ' + f.logic + '=' + f.value;
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
								if(f.value.indexOf('%') >= 0) {
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
		});//ab_date_str
		
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: "USER_OBJECTS",
	   			field: "COUNT(*)",
	   			condition: "OBJECT_TYPE='PACKAGE' AND OBJECT_NAME='FA_BATCH_VIEW_PARAM'"
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);return;
	   			}
				if(localJson.success){
					if(localJson.data != null){
						if(localJson.data=='1'||localJson.data==1){
							if(ab_date_str != null){
								if (condition == ''){
									condition += " fa_batch_view_param.set_param('"+ab_date_str+"')='"+ab_date_str+"'";
								} else {
									condition += " AND fa_batch_view_param.set_param('"+ab_date_str+"')='"+ab_date_str+"'";
								}
							}else{
								if (condition == ''){
									condition += " fa_batch_view_param.set_param(null) is null";
								} else {
									condition += " AND fa_batch_view_param.set_param(null) is null";
								}							
							}
						}
					}
	   			}
				var orderStr = form.getOrderBy(grid);
				if(form.fo_detailGridOrderBy){
					orderStr = (form.fo_detailGridOrderBy!=null && form.fo_detailGridOrderBy!='')?form.fo_detailGridOrderBy:orderStr;
				}
				var gridParam = {caller: caller, condition: condition +' '+ orderStr};
				if(!grid.bigVolume) {
					gridParam.start = 1;
					gridParam.end = 1000;
				}
				grid.store.each(function(){
					this.modified = null;
				});
				grid.GridUtil.loadNewStore(grid, gridParam);
				if(Ext.getCmp('pi_counttotal'))
					Ext.getCmp('pi_counttotal').setValue('0');
				if(Ext.getCmp('pi_amounttotal'))
					Ext.getCmp('pi_amounttotal').setValue('0');
				
	   		}
		});
	},
	
	addKeyBoardEvents: function(){
		var me = this;
		if(Ext.isIE && !Ext.isIE11){
			document.body.attachEvent('onkeydown', function(){//ie的事件名称不同,也不支持addEventListener
				if(window.event.altKey && window.event.ctrlKey && window.event.keyCode == 83){
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/multiform.jsp?formCondition=fo_idIS" + me.fo_id + 
							"&gridCondition=fd_foidIS" + me.fo_id + "&whoami=" + caller);
				}
			});
		} else {
			document.body.addEventListener("keydown", function(e){
				if(Ext.isFF5){//firefox不支持window.event
					e = e || window.event;
				}
				if(e.altKey && e.ctrlKey && e.keyCode == 83){
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/multiform.jsp?formCondition=fo_idIS" + me.fo_id + 
							"&gridCondition=fd_foidIS" + me.fo_id + "&whoami=" + caller);
				}
	    	});
		}
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
	}
});