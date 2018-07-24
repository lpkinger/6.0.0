Ext.define('erp.view.b2c.common.b2cQueryForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpQueryFormPanel',
	id: 'queryform', 
    region: 'north',
    frame : true,
	layout : 'column',
	header: false,////不显示title
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	prevTime:null,
	tbar: [{
		id:'query',
		name: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	handler: function(btn){
    		//限制点击筛选时间间隔不能超过2秒
    		var form=btn.ownerCt.ownerCt;
    		if(form.prevTime==null){
    			form.prevTime=new Date().getTime();
    			form.onQuery();
    		}else {
    			var nowtime=new Date().getTime();
    			if((nowtime-form.prevTime)/1000<2){
    				showError('请控制筛选时间间隔不能小于2秒!');
    				return;
    			}else {
    				form.prevTime=nowtime;
    				form.onQuery();
    			}
    		}
			
    	}
	}, '->'/*, {
		name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-excel',
    	cls: 'x-btn-gray',
    	handler: function(btn){
    		var grid = Ext.getCmp('querygrid');
    		if(grid.xtype == 'erpQueryGridPanel') {
    			var condition = grid.defaultCondition || '';
    			condition = btn.ownerCt.ownerCt.spellCondition(condition);
    			if(Ext.isEmpty(condition)) {
    				condition = grid.emptyCondition || '1=1';
    			}
        		grid.BaseUtil.createExcel(caller, 'detailgrid', condition);
    		} else if(grid.xtype == 'erpDatalistGridPanel') {
    			var condition = grid.getCondition() || '';
    			condition = btn.ownerCt.ownerCt.spellCondition(condition);
    			if(Ext.isEmpty(condition)) {
    				condition = '1=1';
    			}
    			if(typeof grid.getCondition === 'function') {
    				condition += ' and ' + grid.getCondition();
    			}
    			grid.BaseUtil.createExcel(caller, 'datalist', condition);
    		} else {
    			btn.ownerCt.ownerCt.BaseUtil.exportGrid(grid);
    		}
    	}
	}*/, '-',{
		name: 'refresh',
		text: $I18N.common.button.erpRefreshButton,
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray'
	}, {
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	id:'close',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}],
	initComponent : function(){ 
		this.callParent(arguments);
		this.getItemsAndButtons();
	},
	onQuery: function() {
		var grid = Ext.getCmp('querygrid');
		if(!grid){
			grid = Ext.getCmp('grid');
		}
		var form = this;
		var condition = grid.defaultCondition || '';
		condition = form.spellCondition(condition);
		if(Ext.isEmpty(condition)) {
			condition = grid.emptyCondition || '1=1';
		}
		//如果有urlcondition在批处理界面点击筛选时把条件一起带过去
		var urlcondition = getUrlParam('urlcondition');
		if(urlcondition){
			if(condition){
				condition = condition +" and "+urlcondition;
			}else{
				condition = urlcondition;
			}
		}
		form.beforeQuery(caller, condition);//鎵ц鏌ヨ鍓嶉�杈�
		var gridParam = { caller: caller, condition: condition,fields:grid.datafields,tablename:grid.tablename};
		grid.loadNewStore(grid, gridParam);
	},
	spellCondition: function(condition){
		var form = this;
		Ext.each(form.items.items, function(f){
			if(f.logic != null && f.logic != ''&&f.logic!='ignore'){
				if(f.xtype == 'checkbox' && f.value == true){
					if(condition == ''){
						condition += f.logic;
					} else {
						condition += ' AND ' + f.logic;
					}
				} else if(f.xtype == 'datefield' && f.value != null){
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
				} else if(f.xtype == 'yeardatefield' && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + '=' + f.value;
					} else {
						condition += ' AND ' + f.logic + '=' + f.value;
					}
				}else if(f.xtype == 'combo' && f.value == '$ALL'){
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
				}	else if(f.xtype=='adddbfindtrigger' && f.value != null && f.value != ''){
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
					if(f.value != null && f.value != ''){
						var val = String(f.value);
						if(contains(val, 'BETWEEN', true) && contains(val, 'AND', true)){
							if(condition == ''){
								condition += f.logic + " " + f.value;
							} else {
								condition += ' AND (' + f.logic + " " + f.value + ")";
							}
						} else if(f.logic == 'ym_view_param') {
							if(condition == ''){
								condition += " " + f.value;
							} else {
								condition += ' AND (' + f.value + ")";
							}
						} else if(contains(val, '||', true)){
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
								condition += str;
							} else {
								condition += ' AND (' + str + ")";
							}
						} else {
							
							if(val.indexOf('%') >= 0) {
								if(condition == ''){
									condition += f.logic + " like '" + f.value + "'";
								} else {
									condition += ' AND (' + f.logic + " like '" + f.value + "')";
								}
							} else {
								if(f.logic=='CONDITION'){
									if(condition == ''){
										condition +=  f.value ;
									} else {
										condition += ' AND '  + f.value;
									}
								}else{
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
		return condition;
	},
	_noc: 0,
	getItemsAndButtons: function(){
		var me = this;
		var formitem = me.formitems;
		Ext.each(formitem, function(item){
			if(screen.width < 1280){//鏍规嵁灞忓箷瀹藉害锛岃皟鏁村垪鏄剧ず瀹藉害
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
		me.add(formitem);
		me.fireEvent('alladded', me);
		var buttonString = me.buttons;
		if(buttonString != null && buttonString != ''){
			if(contains(buttonString, '#', true)){
				Ext.each(buttonString.split('#'), function(b, index){
					if(!Ext.getCmp(b)){
						var btn = Ext.getCmp('erpVastDealButton');
						if(btn){
							btn.ownerCt.insert(2, {
								xtype: b
							});
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
	beforeQuery: function(call, cond) {
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
				}
			}
		});
	}
});