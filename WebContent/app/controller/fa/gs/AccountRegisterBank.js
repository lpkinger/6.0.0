Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.AccountRegisterBank', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.gs.AccountRegisterBank','core.form.Panel','fa.gs.AccountRegisterTree','common.datalist.GridPanel','core.button.Refresh',
    		'core.button.Add','core.button.Close','core.form.ConDateField','core.button.Query','common.datalist.Toolbar',
    		'core.trigger.DbfindTrigger','core.form.YnField', 'core.form.SeparNumber'
    	],
    refs: [{
    	ref: 'centerpanel',
    	selector: '#centerpanel'
    }],
    init:function(){
    	var me = this;
    	me.datamanager = [];
    	this.control({
    		'accountregistertree': {
    			itemmousedown: function(selModel, record){
    				if (me.isGroup === undefined) {
    					var d = record.raw ? record.raw.data : record.data.data;
    					if(d.ma_id) {
    						me.isGroup = true;
    					} else {
    						me.isGroup = false;
    					}
    				}
    				me.loadTab(selModel, record);
    				me.lastSelected = record;
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpQueryButton': {
    			click : function(btn) {
    				var _win = this.getCenterpanel().getEl().down('iframe').dom.contentWindow;
    				if(_win.Ext && caller == _win.caller) {// caller一致就不用刷咯
    					var grid = _win.Ext.getCmp('grid');
    					if(grid) {
    						_win.caller = caller;
    						var d = Ext.getCmp('ar_date'), date1 = d.firstVal,date2 = d.secondVal;
    						var condition = date1 ? ("ar_date BETWEEN to_date('" + Ext.Date.toString(date1) + " 00:00:00','yyyy-MM-dd HH24:mi:ss') AND to_date('" 
    								+ Ext.Date.toString(date2) + " 23:59:59','yyyy-MM-dd HH24:mi:ss')") : null;
    						if (me.treeconfig && me.treeconfig != '' && me.treeconfig != null){
    							condition = (condition ? (condition + ' and ') : '' ) + "ar_cateid=" + me.treeconfig;
    						}
    						grid.defaultCondition=condition;
    						grid.getCount(caller, condition);
    						this.FormUtil.getFieldValue('Category', 'nvl(ca_nowbalance,0)+nvl(ca_nowbalance2,0)', 'ca_id=' + me.treeconfig, 'ca_balance');
    					}
    				} 
				}
    		},
    		'erpAddButton':{
    			click: function(btn){
    				var record = me.lastSelected,
    					url = 'jsps/fa/gs/accountRegister.jsp?whoami=AccountRegister!Bank';
    				if(record) {
    					var data = record.raw ? record.raw.data : record.data.data;
    					url += '&ar_cateid=' + data.ca_id;
    					url += '&ar_accountcode=' + data.ca_code;
    					url += '&ar_accountname=' + encodeURIComponent(data.ca_description);
    					url += '&ar_accountcurrency=' + data.ca_currency;
    					url += '&ar_accountrate=' +data.ca_currencyrate;
    					url += '&ar_currencytype=' +data.ca_currencytype;
    					url += '&ar_arapcurrency=' + data.ca_currency;
    					url += '&ar_araprate=1';
    				}
    				me.FormUtil.onAdd('addAccountRegister', '新增银行存款登记', url);
    			}
    		},
    		'erpRefreshButton': {
    			click: function(btn){
    				 Ext.Ajax.request({
     					url: basePath + 'fa/gs/copyAccountRegister/refreshQuery.action',
     					params: {
     						
 						},
     					method: 'post',        					
     					callback: function(opt, s, r) {        						
     						var rs = Ext.decode(r.responseText);
     						if(rs.exceptionInfo){
				   				showError(rs.exceptionInfo);
				   				return "";
				   			}
			    			if(rs.success){
				   				Ext.Msg.alert("提示", "刷新成功!", function(){
				   					window.location.reload();
				   				});
				   			}
     					}
     				});
    			}
    		},
    		'field[name=ca_balance]' : {
    			afterrender : function(f) {
    				f.setFieldStyle({
    					'color' : 'red'
    				});
    			}
    		}
    	});
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getCurrentStore: function(value){
		var grid = Ext.getCmp('grid');
		var items = grid.store.data.items;
		var array = new Array();
		var o = null;
		Ext.each(items, function(item, index){
			o = new Object();
			o.selected = false;
			if(index == 0){
				o.prev = null;
			} else {
				o.prev = items[index-1].data[keyField];
			}
			if(index == items.length - 1){
				o.next = null;
			} else {
				o.next = items[index+1].data[keyField];
			}
			var v = item.data[keyField];
			o.value = v;
			if(v == value)
				o.selected = true;
			array.push(o);
		});
		return array;
	},
    loadTab: function(selModel, record){
    	var me = this, tree = Ext.getCmp('tree-panel'), 
    		data = record.raw ? record.raw.data : record.data.data;
    	if (!record.get('leaf')) {
    		if(record.isExpanded() && record.childNodes.length > 0){//是根节点，且已展开
				record.collapse(true, true);//收拢
			} else {//未展开
				//看是否加载了其children
				if(record.childNodes.length == 0){
					//从后台加载
		            tree.setLoading(true, tree.body);
					Ext.Ajax.request({//拿到tree数据
			        	url : basePath + 'fa/gs/getCategoryBankTree.action',
			        	params: {
			        		master: data.ma_name || data.CURRENTMASTER,
			        		parentid: data.ca_id || 0
			        	},
			        	async: false,
			        	callback : function(options,success,response){
			        		tree.setLoading(false);
			        		var res = new Ext.decode(response.responseText);
			        		if(res.tree){
			        			record.appendChild(res.tree);
			        			record.expand(false, true);//展开
			        			if(data.ma_id)
			        				me.refreshDatalist(data);
			        		} else if(res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        		}
			        	}
			        });
				} else {
					record.expand(false, true);//展开
					if(data.ma_id)
        				me.refreshDatalist(data);
				}
			}
    	} else {
    		this.refreshDatalist(data);
    	}
    },
    refreshDatalist : function(data) {
    	var me = this;
    	
    	var ca_id = data.ca_id || 0,
    		condition = 'ar_cateid=' + ca_id;
    	me.treeconfig = ca_id;
    	var dd = Ext.getCmp('ar_date'),
    		date1 = dd.firstVal, 
    		date2 = dd.secondVal;
    	if(date1) {
    		condition += " and ar_date BETWEEN to_date('" + Ext.Date.toString(date1) 
			+ " 00:00:00','yyyy-MM-dd HH24:mi:ss') AND to_date('" 
			+ Ext.Date.toString(date2) + " 23:59:59','yyyy-MM-dd HH24:mi:ss')";
    	}
    	if (this.isGroup) {
    		if(data.ma_type == 2 && data.ma_soncode) {
    			var s = data.ma_soncode.split(','), m = [];
    			for(i in s) {
    				m.push('\'' + s[i] + '\'');
    			}
    			condition = 'CURRENTMASTER in(' + m.join(',') + ')';
    		} else if(data.ma_type == 3) {
    			condition = 'CURRENTMASTER=\'' + (data.ma_name || data.CURRENTMASTER) + '\'';
    		} else {
    			condition += ' AND CURRENTMASTER=\'' + (data.ma_name || data.CURRENTMASTER) + '\'';
    		}
    	}
		this.FormUtil.getFieldValue('Category', 'nvl(ca_nowbalance,0)+nvl(ca_nowbalance2,0)', 'ca_id=' + ca_id, 'ca_balance');
		this.FormUtil.getFieldValue('Category', 'ca_bankaccount', 'ca_id=' + ca_id, 'ca_bankaccount');
		var _win = this.getCenterpanel().getEl().down('iframe').dom.contentWindow;
		if(_win.Ext && caller == _win.caller) {// caller一致就不用刷咯
			var grid = _win.Ext.getCmp('grid');
			if(grid) {
				_win.caller = caller;
				_win.condition = condition;
				_win._noc=1;
				grid.defaultCondition = condition;
				grid.getCount(caller, condition);
			}
		} else {
			this.getCenterpanel().getEl().down('iframe').dom.contentWindow.location.href = 
    			basePath + 'jsps/common/datalist.jsp?whoami=' + caller + '&urlcondition=' +
    			condition+"&_noc=1";
		}
    }
});