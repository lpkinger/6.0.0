Ext.define('erp.view.pm.bom.CompareBom.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpQueryFormPanel1',
	id: 'queryform', 
    region: 'north',
    frame : true,
	layout : 'column',
	header: false,//不显示title
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
	tbar: [{
		id:'query',
		name: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray'
    	/*handler: function(btn){
			btn.ownerCt.ownerCt.onQuery();
    	}*/
	},{
		margin:'0 0 0 10',
		name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	handler: function(btn){
    		var grid = Ext.getCmp('querygrid');
    		grid.BaseUtil.exportGrid(grid);
    	}
	}, '->', {
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}],
	initComponent : function(){ 
		this.getItemsAndButtons();
		this.callParent(arguments);
		this.addKeyBoardEvents();
	},
	onQuery: function() {
		var grid = Ext.getCmp('querygrid');
		var form = this;
		var condition = grid.defaultCondition || '';
		condition = form.spellCondition(condition);
		form.beforeQuery(caller, condition);//执行查询前逻辑
		var gridParam = {caller: caller, condition: condition};
		grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
	},
	spellCondition: function(condition){
		var form = this;
		Ext.each(form.items.items, function(f){
			if(f.logic != null && f.logic != ''){
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
				} else {
					if(f.value != null && f.value != ''){
						if(contains(f.value, 'BETWEEN', true) && contains(f.value, 'AND', true)){
							if(condition == ''){
								condition += f.logic + " " + f.value;
							} else {
								condition += ' AND (' + f.logic + " " + f.value + ")";
							}
						} else if(contains(f.value, '||', true)){
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
							if(condition == ''){
								condition += f.logic + "='" + f.value + "'";
							} else {
								condition += ' AND (' + f.logic + "='" + f.value + "')";
							}
						}
					}
				}
			}
		});
		return condition;
	},
	getItemsAndButtons: function(){
		var me = this;
		this.setLoading(true);
		Ext.Ajax.request({//拿到form的items
        	url : basePath + 'common/singleFormItems.action',
        	params: {
        		caller: caller, 
        		condition: ''
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.setLoading(false);
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
        		Ext.each(res.items, function(item){
        			item.labelAlign = 'right';
    				item.fieldStyle = 'background:#ffffff;color:#515151;';
    				if(item.xtype == 'checkbox') {
						item.fieldStyle = 'margin:0 0 0 80px';
    					item.focusCls = '';
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
        	}
        });
	},
	addKeyBoardEvents: function(){
		var me = this;
		if(Ext.isIE && !Ext.isIE11){
			document.body.attachEvent('onkeydown', function(){//ie的事件名称不同,也不支持addEventListener
				if(window.event.altKey && window.event.ctrlKey && window.event.keyCode == 83){
					if(Ext.ComponentQuery.query('gridpanel').length > 0){//有grid
						me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/multiform.jsp?formCondition=fo_idIS" + me.fo_id + 
								"&gridCondition=fd_foidIS" + me.fo_id + "&whoami=" + caller);
					} else {
						me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + 
								"&gridCondition=fd_foidIS" + me.fo_id);
					}
				}
			});
			document.body.attachEvent("onmouseover", function(){
				if(window.event.ctrlKey){
					var e = window.event;
					me.Contextvalue = e.target.textContent == "" ? e.target.value : e.target.textContent;
					textarea_text = parent.document.getElementById("textarea_text");
					textarea_text.value = me.Contextvalue;
						textarea_text.focus();
						textarea_text.select();
					}
			});
		} else {
			document.body.addEventListener("keydown", function(e){
				if(Ext.isFF5){//firefox不支持window.event
					e = e || window.event;
				}
				if(e.altKey && e.ctrlKey && e.keyCode == 83){
					if(Ext.ComponentQuery.query('gridpanel').length > 0){//有grid
						me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/multiform.jsp?formCondition=fo_idIS" + me.fo_id + 
								"&gridCondition=fd_foidIS" + me.fo_id + "&whoami=" + caller);
					} else {
						me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + 
								"&gridCondition=fd_foidIS" + me.fo_id);
					}
				}
			});
			document.body.addEventListener("mouseover", function(e){
				if(Ext.isFF5){
					e = e || window.event;
				}
				if(e.ctrlKey){
					me.Contextvalue = e.target.textContent == "" ? e.target.value : e.target.textContent;
					textarea_text = parent.document.getElementById("textarea_text");
					textarea_text.value = me.Contextvalue;
						textarea_text.focus();
						textarea_text.select();
					}
			});
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