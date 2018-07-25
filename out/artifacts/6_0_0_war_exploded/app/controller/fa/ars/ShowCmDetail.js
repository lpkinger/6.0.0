Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.ShowCmDetail', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.trigger.DbfindTrigger', 'fa.ars.cmQuery.CmDetailGrid', 'fa.ars.cmQuery.ShowCmDetail', 'core.form.MonthDateField', 
            'core.form.ConMonthDateField', 'core.form.YearDateField'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'cmdetailgrid':{
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
    				var grid = Ext.getCmp('cmdetailgrid');
    				me.BaseUtil.exportGrid(grid, '应收总账明细'+'-'+yearmonth+'-'+custname+'-'+currency+'_','  客户名称:'+custname+'    币别:'+currency+'    期间:'+yearmonth);
    			}
    		},
    		'button[name=query]':{
    			click:function(btn){
    				var me = this;
    				if(Ext.getCmp(btn.getId() + '-filter')){
    					Ext.getCmp(btn.getId() + '-filter').show();
    				}else{
    					var filter = me.createFilterPanel(btn);
    					filter.show();
    				}
    			}
    		}
    	});
    },
    //弹出筛选框
    createFilterPanel:function(btn){
    	var me = this;
    	var filter = Ext.create('Ext.Window', {
    		id: btn.getId() + '-filter',
    		style: 'background:#f1f1f1',
    		title: '筛选条件',
    		width: 300,
    		modal:true,
    		height: 250,
    	    layout: 'column',
    	    defaults: {
    	    	margin: '2 2 2 10'
    	    },
    	    items: [{
				xtype: 'checkbox',
				id: 'showarmsg',
				name: 'showarmsg',
				checked:true,
				columnWidth: .71,
				boxLabel: '显示应收发票信息'
			},{
				xtype: 'checkbox',
				id: 'showotarmsg',
				name: 'showotarmsg',
				checked:true,
				columnWidth: .71,
				boxLabel: '显示其它应收信息'
			},{
				xtype: 'checkbox',
				id: 'showrbmsg',
				name: 'showrbmsg',
				checked:true,
				columnWidth: .71,
				boxLabel: '显示收款单信息'
			},{
				xtype: 'checkbox',
				id: 'showprerecmsg',
				name: 'showprerecmsg',
				checked:true,
				columnWidth: .71,
				boxLabel: '显示预收账款信息'
			},{
				xtype: 'checkbox',
				id: 'showgsmsg',
				name: 'showgsmsg',
				checked:true,
				columnWidth: .71,
				boxLabel: '显示发出商品信息'
			},{
				xtype: 'checkbox',
				id: 'showdemsg',
				name: 'showdemsg',
				columnWidth: .71,
				boxLabel: '显示销售发票明细'
			}/*,{
				xtype: 'checkbox',
				id: 'showdemsg',
				name: 'showdemsg',
				columnWidth: .71,
				boxLabel: '显示应收款转销明细'
			}*/],
			buttonAlign: 'center',
    	    buttons: [{
	    		text: '确定',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		handler: function(btn) {

	    			var showArDetailVal = Ext.getCmp('showdemsg').getValue();
	    			var grid = Ext.getCmp('cmdetailgrid');
	    			var fl = btn.ownerCt.ownerCt;
	    			var	con = me.getConfig(fl);
	    			if(showArDetailVal){
	    				grid.columns = grid.detailColumns;
	    				me.getConditionDetail(con,'fa/ars/CmQueryController/getCmDetailByIdDetail.action');
	    			}else{
	    				grid.columns = grid.defaultColumns;
	    				me.getConditionDetail(con,'fa/ars/CmQueryController/getCmDetailById.action');
	    			}
					//隐藏筛选框
					
					fl.hide();
	    		}
	    	},{
	    		text: '关闭',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		handler: function(btn) {
	    			
	    			var fl = btn.ownerCt.ownerCt;
	    			fl.hide();
	    		}
	    	}]
    	});
		return filter;
    
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
    			'cmid':cmid,
    			'yearmonth':yearmonth,
    			'currency':currency,
    			'custcode':custcode,
    			'chkumio':chkumio,
    			'config':{
    				'showarmsg':true,
    				'showotarmsg':true,
    				'showrbmsg':true,
    				'showgsmsg':true,
    				'showprerecmsg':true,
    				'showdemsg': false
    			}
    			};
    	me.query(cond);
    },
    query: function(cond) {
    	
    	var grid = Ext.getCmp('cmdetailgrid');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/ars/CmQueryController/getCmDetailById.action',
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
    			'cmid':cmid,
    			'yearmonth':yearmonth,
    			'currency':currency,
    			'custcode':custcode,
    			'chkumio':chkumio,
    			'config':config
    			};
    	me.queryDetail(cond,url);
    },
    queryDetail: function(cond,url) {
    	
    	var grid = Ext.getCmp('cmdetailgrid');
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
    					    	name: 'tb_code',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_kind',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_remark',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_vouc',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_date',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_aramount',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_inoutno',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_pdno',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_ordercode',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_prodcode',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_qty',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_price',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_rbamount',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_aramounts',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_rbamounts',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_balance',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_index',
    					    	type: 'number'
    					    },{
    					    	name: 'tb_id',
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
    		var value = Number(record.data['tb_id']);
    		var me = this;
    		if(value>0){
    			var url='', k = record.get('tb_kind'), t = record.get('tb_table');
    			var keyField ='';
    			var caller = '';
    			var pfField = '';
    			
    			if(k == '应收发票'){
    				url ='jsps/fa/ars/arbill.jsp?whoami=ARBill!IRMA';
    				keyField = 'ab_id';
    				pfField = 'abd_abid';
    				caller = 'ARBill!IRMA';
    			}else if(k=='收款单'){
    				url ='jsps/fa/ars/recBalance.jsp?whoami=RecBalance!PBIL';
    				keyField = 'rb_id';
    				pfField = 'rbd_rbid';
    				caller = 'RecBalance!PBIL';
    			}else if(k=='预收退款单'){
    				url ='jsps/fa/ars/preRec.jsp?whoami=PreRec!Ars!DEPR';
    				keyField = 'pr_id';
    				pfField = 'prd_prid';
    				caller = 'PreRec!Ars!DEPR';
    			}else if(k=='应收退款单' && t == 'recbalance'){
    				url ='jsps/fa/ars/recBalanceTK.jsp?whoami=RecBalance!TK';
    				keyField = 'rb_id';
    				pfField = 'rbd_rbid';
    				caller = 'RecBalance!TK';
    				
    			}else if(k=='其它应收单'){
    				url ='jsps/fa/ars/arbill.jsp?whoami=ARBill!OTRS';
    				keyField = 'ab_id';
    				pfField = 'abd_abid';
    				caller = 'ARBill!OTRS';
    			}else if(k=='发出商品'){
    				url ='jsps/fa/ars/goodsSend.jsp';
    				keyField = 'gs_id';
    				pfField = 'gsd_gsid';
    				caller = 'GoodsSendGs';
    			}else if(k=='出货单'){
    				url ='jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!Sale';
    				keyField = 'pi_id';
    				pfField = 'pd_piid';
    				caller = 'ProdInOut!Sale';
    			}else if(k=='销售退货单'){
    				url ='jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!SaleReturn';
    				keyField = 'pi_id';
    				pfField = 'pd_piid';
    				caller = 'ProdInOut!SaleReturn';
    			}else if(k=='预收款'){
    				url ='jsps/fa/ars/preRec.jsp?whoami=PreRec!Ars!DERE';
    				keyField = 'pr_id';
    				pfField = 'prd_prid';
    				caller = 'PreRec!Ars!DERE';
    			}else if(k=='预收冲应收'){
    				url ='jsps/fa/ars/recBalancePRDetail.jsp?whoami=RecBalance!PTAR';
    				keyField = 'rb_id';
    				pfField = 'rbd_rbid';
    				caller = 'RecBalance!PTAR';
    			}else if(k=='冲应收款'){
    				url ='jsps/fa/ars/recBalance.jsp?whoami=RecBalance!IMRE';
    				keyField = 'rb_id';
    				pfField = 'rbd_rbid';
    				caller = 'RecBalance!IMRE';
    			}else if(k=='应收款转销' && t == 'recbalance'){
    				url ='jsps/fa/ars/recBalance.jsp?whoami=RecBalance!ARRM';
    				keyField = 'rb_id';
    				pfField = 'rbd_rbid';
    				caller = 'RecBalance!ARRM';
    			}else if(k=='应收款转销' && t == 'arbill'){
    				url ='jsps/fa/ars/arbill.jsp?whoami=ARBill!IRMA';
    				keyField = 'ab_id';
    				pfField = 'abd_abid';
    				caller = 'ARBill!IRMA';
    			}else if(k=='应收冲应付' && t == 'recbalance'){
    				url ='jsps/fa/ars/recBalanceAP.jsp?whoami=RecBalance!RRCW';
    				keyField = 'rb_id';
    				pfField = 'rbd_rbid';
    				caller = 'RecBalance!RRCW';
    			}
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
        	    	var myurl = '';
        	    	if(me.BaseUtil.contains(url, '?', true)){
        	    		myurl = url + '&formCondition='+formCondition+'&gridCondition='+gridCondition;
        	    	} else {
        	    		myurl = url + '?formCondition='+formCondition+'&gridCondition='+gridCondition;
        	    	}
        	    	myurl += "&datalistId=" + main.getActiveTab().id;
    	    		panel = {       
        	    			title :record.data['tb_kind']+'('+title+')',
        	    			tag : 'iframe',
        	    			tabConfig:{tooltip:me.BaseUtil.getActiveTab().tabConfig.tooltip+'('+keyField + "=" + value+')'},
        	    			frame : true,
        	    			border : false,
        	    			layout : 'fit',
        	    			iconCls : 'x-tree-icon-query',
        	    			html : '<iframe id="iframe_maindetail_'+caller+"_"+value+'" src="' + myurl + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
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
    	/*var tab = main.getComponent(o); */
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
//    getCurrentStore: function(value){
//    	var grid = Ext.getCmp('cmdetailgrid');
//		var items = grid.store.data.items;
//		var array = new Array();
//		var o = null;
//		Ext.each(items, function(item, index){
//			o = new Object();
//			o.selected = false;
//			if(index == 0){
//				o.prev = null;
//			} else {
//				o.prev = items[index-1].data[keyField];
//			}
//			if(index == items.length - 1){
//				o.next = null;
//			} else {
//				o.next = items[index+1].data[keyField];
//			}
//			var v = item.data[keyField];
//			o.value = v;
//			if(v == value)
//				o.selected = true;
//			array.push(o);
//		});
//		return array;
//    }
});