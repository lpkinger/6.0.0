Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.ShowArDetail', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.trigger.DbfindTrigger', 'fa.gs.arQuery.ArDetailGrid', 'fa.gs.arQuery.ShowArDetail', 'core.form.MonthDateField', 
            'core.form.ConMonthDateField', 'core.form.YearDateField'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'ardetailgrid':{
    			itemclick:this.onGridItemClick
    		},
    		'button[id=close]': {
    			afterrender: function() {
    				setTimeout(function(){
    					me.getCondition();
    				},200);
    			}
    		},
    		'button[name=export]': {
    			click: function() {
    				var grid = Ext.getCmp('ardetailgrid');
    				me.BaseUtil.exportGrid(grid, '银行收支明细'+'-'+yearmonth+'-'+accountname+'-'+currency+'_','  账户描述:'+accountname+'    币别:'+currency+'    期间:'+yearmonth);
    			}
    		},
    		'button[name=query]':{
    			click:function(btn){
    				var me = this;
    				me.getCondition();
    			}
    		}
    	});
    },
    getConfig: function(pl) {
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
    //得到界面上够选框选择条件  并执行查找   没有勾选显示明细的情况
    getCondition:function(){
    	var me = this;
    	var cond = {
    			'amid':amid,
    			'yearmonth':yearmonth,
    			'currency':currency,
    			'accountcode':accountcode,
    	};
    	me.query(cond);
    },
    query: function(cond) {
    	var grid = Ext.getCmp('ardetailgrid');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/gs/ArQueryController/getArDetailById.action',
    		params: {
    			condition: Ext.encode(cond)
    		},
    		callback: function(opt, s, r) {
    			var res = Ext.decode(r.responseText);
    			if(grid && res.data) {
    				grid.store.loadData(res.data);
    			}
    			grid.setLoading(false);
    		}
    	});
    },
    //得到界面上够选框选择条件  并执行查找   勾选显示明细的情况
    getConditionDetail:function(config,url){
    	var me = this;
    	var cond = {
    			'amid':amid,
    			'yearmonth':yearmonth,
    			'currency':currency,
    			'accountcode':accountcode
    	};
    	me.queryDetail(cond,url);
    },
    queryDetail: function(cond,url) {
    	var grid = Ext.getCmp('ardetailgrid');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + url,
    		params: {
    			condition: Ext.encode(cond)
    		},
    		callback: function(opt, s, r) {
    			var res = Ext.decode(r.responseText);
    			if(grid && res.data) {
    				var store =  Ext.create('Ext.data.Store', {
    					  fields:[{
    					    	name: 'ar_code',
    					    	type: 'string'
    					    },{
    					    	name: 'ar_type',
    					    	type: 'string'
    					    },{
    					    	name: 'ar_date',
    					    	type: 'string'
    					    },{
    					    	name: 'ar_custcode',
    					    	type: 'string'
    					    },{
    					    	name: 'ar_custname',
    					    	type: 'string'
    					    },{
    					    	name: 'ar_vendcode',
    					    	type: 'string'
    					    },{
    					    	name: 'ar_vendname',
    					    	type: 'string'
    					    },{
    					    	name: 'ar_payment',
    					    	type: 'string'
    					    },{
    					    	name: 'ar_deposit',
    					    	type: 'string'
    					    },{
    					    	name: 'ar_balance',
    					    	type: 'string'
    					    },{
    					    	name: 'ar_recordman',
    					    	type: 'string'
    					    },{
    					    	name: 'ar_vouchercode',
    					    	type: 'string'
    					    },{
    					    	name: 'ar_sourcetype',
    					    	type: 'string'
    					    },{
    					    	name: 'ar_source',
    					    	type: 'string'
    					    },{
    					    	name: 'ar_index',
    					    	type: 'number'
    					    },{
    					    	name: 'ar_id',
    					    	type:'number'
    					    }],
    			        data: res.data
    				});
    				grid.reconfigure(store,grid.columns);
    			}
    			grid.setLoading(false);
    		}
    	});
    },
    onGridItemClick:function(selModel, record){
    		var value = Number(record.data['ar_id']);
    		var me = this;
    		if(value>0){
    			var url='jsps/fa/gs/accountRegister.jsp';
    			var keyField ='ar_id';
    			var caller = 'AccountRegister!Bank';
    			var pfField = 'ard_arid';
    		
            	var formCondition = keyField + "IS" + value ;
            	var gridCondition = pfField + "IS" + value;
    			var panelId = caller + keyField + "_" + value + gridCondition;
    			var panel = Ext.getCmp(panelId);
            	var main = parent.Ext.getCmp("content-panel");
            	if(!main){
    				main = parent.parent.Ext.getCmp("content-panel");
    			}
            	if(!panel){ 
            		var title = "";
        	    	if (value.toString().length>4) {
        	    		 title = value.toString().substring(value.toString().length-4);	
        	    	} else {
        	    		title = value;
        	    	}
        	    	url += "?whoami=" + caller + '&formCondition='+formCondition+'&gridCondition='+gridCondition;
    	    		panel = {       
        	    			title :record.data['ar_type']+'('+title+')',
        	    			tag : 'iframe',
        	    			tabConfig:{tooltip:me.BaseUtil.getActiveTab().tabConfig.tooltip+'('+keyField + "=" + value+')'},
        	    			frame : true,
        	    			border : false,
        	    			layout : 'fit',
        	    			iconCls : 'x-tree-icon-query',
        	    			html : '<iframe id="iframe_maindetail_'+caller+"_"+value+'" src="' + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
        	    			closable : true,
        	    			listeners : {
        	    				close : function(){
        	    					if(!main){
        	    						main = parent.parent.Ext.getCmp("content-panel");
        	    					}
        	    			    	main.setActiveTab(main.getActiveTab().id); 
        	    				}
        	    			} 
        	    	};
        	    	this.openTab(panel, panelId);
            	}else{ 
        	    	main.setActiveTab(panel); 
            	} 
    	}
    },
    openTab : function (panel,id){ 
    	var o = (typeof panel == "string" ? panel : id || panel.id); 
    	var main = parent.Ext.getCmp("content-panel"); 
    	if(!main) {
    		main =parent.parent.Ext.getCmp("content-panel"); 
    	}
    	var tab = main.getComponent(o); 
    	if (tab) { 
    		main.setActiveTab(tab); 
    	} else if(typeof panel!="string"){ 
    		panel.id = o; 
    		var p = main.add(panel); 
    		main.setActiveTab(p); 
    	} 
    }
});