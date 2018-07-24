Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.ShowVmCopDetail', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['core.trigger.DbfindTrigger', 'fa.arp.vmCopQuery.VmDetailGrid', 'fa.arp.vmCopQuery.ShowVmDetail', 'core.form.MonthDateField', 
            'core.form.ConMonthDateField', 'core.form.YearDateField'],
    init:function(){
    	var me = this;
    	this.control({
    		'vmdetailgrid':{
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
    				var grid = Ext.getCmp('vmcopdetailgrid');
    				me.BaseUtil.exportGrid(grid, '应付总账明细'+'-'+yearmonth+'-'+vendname+'-'+currency+'_'+cop,' 公司:'+cop+'  供应商名称:'+vendname+'    币别:'+currency+'    期间:'+yearmonth);
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
				id: 'showapmsg',
				name: 'showapmsg',
				checked:true,
				columnWidth: .71,
				boxLabel: '显示应付发票信息'
			},{
				xtype: 'checkbox',
				id: 'showotapmsg',
				name: 'showotapmsg',
				checked:true,
				columnWidth: .71,
				boxLabel: '显示其它应付信息'
			},{
				xtype: 'checkbox',
				id: 'showpbmsg',
				name: 'showpbmsg',
				checked:true,
				columnWidth: .71,
				boxLabel: '显示付款单信息'
			},{
				xtype: 'checkbox',
				id: 'showprepaymsg',
				name: 'showprepaymsg',
				checked:true,
				columnWidth: .71,
				boxLabel: '显示预付账款信息'
			},{
				xtype: 'checkbox',
				id: 'showesmsg',
				name: 'showesmsg',
				checked:true,
				columnWidth: .71,
				boxLabel: '显示应付暂估信息'
			},{
				xtype: 'checkbox',
				id: 'showdemsg',
				name: 'showdemsg',
				columnWidth: .71,
				boxLabel: '显示采购发票明细'
			}],
			buttonAlign: 'center',
    	    buttons: [{
	    		text: '确定',
	    		width: 60,
	    		cls: 'x-btn-blue',
	    		handler: function(btn) {

	    			var showApDetailVal = Ext.getCmp('showdemsg').getValue();
	    			var grid = Ext.getCmp('vmcopdetailgrid');
	    			var fl = btn.ownerCt.ownerCt;
	    			var	con = me.getConfig(fl);
	    			if(showApDetailVal){
	    				
	    				grid.columns = grid.detailColumns;
	    				me.getConditionDetail(con,'fa/arp/VmQueryController/getVmCopDetailByIdDetail.action');
	    			}else{
	    				grid.columns = grid.defaultColumns;
	    				me.getConditionDetail(con,'fa/arp/VmQueryController/getVmCopDetailById.action');
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
    //得到界面上够选框选择条件  并执行查找
    getCondition:function(){
    	var me = this;
    	var cond = {
    			'vmid':vmid,
    			'yearmonth':yearmonth,
    			'currency':currency,
    			'vendcode':vendcode,
    			'cop':cop,
    			'chkumio':chkumio,
    			'config':{
    				'showapmsg':true,
    				'showotapmsg':true,
    				'showpbmsg':true,
    				'showesmsg':true,
    				'showprepaymsg':true
    			}
    			};
    	me.query(cond);
    },
    query: function(cond) {
    	
    	var grid = Ext.getCmp('vmcopdetailgrid');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + 'fa/arp/VmQueryController/getVmCopDetailById.action',
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
    			'vmid':vmid,
    			'yearmonth':yearmonth,
    			'currency':currency,
    			'cop':cop,
    			'vendcode':vendcode,
    			'chkumio':chkumio,
    			'config':config
    			};
    	me.queryDetail(cond,url);
    },
    queryDetail: function(cond,url) {
    	
    	var grid = Ext.getCmp('vmcopdetailgrid');
    	grid.setLoading(true);
    	Ext.Ajax.request({
    		url: basePath + url,
    		params: {
    			condition: Ext.encode(cond)
    		},
    		callback: function(opt, s, r) {
    			var res = Ext.decode(r.responseText);
    			
    			if(grid && res.data) {
//    				grid.store.loadData(res.data);
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
    					    	name: 'tb_date',
    					    	type: 'string'
    					    },{
    					    	name: 'tb_apamount',
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
    					    	name: 'tb_pbamount',
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
			var url='';
			var keyField ='';
			var caller = '';
			var pfField = '';
			
			if(record.data['tb_kind']=='应付发票'){
				url ='jsps/fa/ars/apbill.jsp?whoami=APBill!CWIM';
				keyField = 'ab_id';
				pfField = 'abd_abid';
				caller = 'APBill!CWIM';
				
			}else if(record.data['tb_kind']=='付款单'){
				url ='jsps/fa/arp/paybalance.jsp?whoami=PayBalance';
				keyField = 'pb_id';
				pfField = 'pbd_pbid';
				caller = 'PayBalance';
				
			}else if(record.data['tb_kind']=='冲应付款'){
				url ='jsps/fa/arp/paybalance.jsp?whoami=PayBalance!CAID';
				keyField = 'pb_id';
				pfField = 'pbd_pbid';
				caller = 'PayBalance';
				
			}else if(record.data['tb_kind']=='其它应付单'){
				url ='jsps/fa/ars/apbill.jsp?whoami=APBill!OTDW';
				keyField = 'ab_id';
				pfField = 'abd_abid';
				caller = 'APBill!OTDW';
			}else if(record.data['tb_kind']=='应付暂估'){
				url ='jsps/fa/arp/estimate.jsp';
				keyField = 'es_id';
				pfField = 'esd_esid';
				caller = 'Estimate';
			}else if(record.data['tb_kind']=='采购验收单'){
				url ='jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!PurcCheckin';
				keyField = 'pi_id';
				pfField = 'pd_piid';
				caller = 'ProdInOut!PurcCheckin';
			}else if(record.data['tb_kind']=='委外验收单'){
				url ='jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!OutsideCheckIn';
				keyField = 'pi_id';
				pfField = 'pd_piid';
				caller = 'ProdInOut!OutsideCheckIn';
			}else if(record.data['tb_kind']=='采购验退单'){
				url ='jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!PurcCheckout';
				keyField = 'pi_id';
				pfField = 'pd_piid';
				caller = 'ProdInOut!PurcCheckout';
			}else if(record.data['tb_kind']=='委外验退单'){
				url ='jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!OutesideCheckReturn';
				keyField = 'pi_id';
				pfField = 'pd_piid';
				caller = 'ProdInOut!OutesideCheckReturn';
			}else if(record.data['tb_kind']=='预付款'){
				url ='jsps/fa/arp/prepay.jsp?whoami=PrePay!Arp!PAMT';
				keyField = 'pp_id';
				pfField = 'ppd_ppid';
				caller = 'PrePay!Arp!PAMT';
			}else if(record.data['tb_kind']=='预付冲应付'){
				url ='jsps/fa/arp/payBalancePRDetail.jsp?whoami=PayBalance!Arp!PADW';
				keyField = 'pb_id';
				pfField = 'pbd_pbid';
				caller = 'PayBalance!Arp!PADW';
			}else if(record.data['tb_kind']=='应付款转销'){
				url ='jsps/fa/arp/paybalance.jsp?whoami=PayBalance!APRM';
				keyField = 'pp_id';
				pfField = 'ppd_ppid';
				caller = 'PayBalance!APRM';
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

});