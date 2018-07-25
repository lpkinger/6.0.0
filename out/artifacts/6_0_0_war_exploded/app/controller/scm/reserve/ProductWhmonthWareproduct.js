Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.ProductWhmonthWareproduct', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.reserve.ProductWhmonth','common.datalist.GridPanel','common.datalist.Toolbar','common.batchDeal.Form',
    		'core.trigger.DbfindTrigger','core.form.ConDateField','core.form.MonthDateField',
    		'core.button.TurnPWHMonthAdjust','core.form.ConMonthDateField'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpDatalistGridPanel': {
    			itemclick: this.onGridItemClick,
    			afterrender:function(grid){
    				Ext.getCmp('datalistexport').setDisabled(true);
    				if(Ext.isIE && !Ext.isIE11){
    					document.body.attachEvent('onkeydown', function(){
    						if(window.event.ctrlKey && window.event.keyCode == 67){//Ctrl + C
    							var e = window.event;
    							if(e.srcElement) {
    								window.clipboardData.setData('text', e.srcElement.innerHTML);
    							}
    						}
    					});
    				} else {
    					grid.getEl().dom.addEventListener("mouseover", function(e){
        					if(e.ctrlKey){
        						 var Contextvalue=e.target.textContent==""?e.target.value:e.target.textContent;
        						 textarea_text = parent.document.getElementById("textarea_text");
        						 textarea_text.value=Contextvalue;
        					     textarea_text.focus();
        					     textarea_text.select();
        					}
        		    	});
    				}
    			}
    		},
    		'button[id=query]':{
    			beforerender:function(btn){
    				btn.handler=function(){
    					var grid = Ext.getCmp('grid');
        				var condition = btn.ownerCt.ownerCt.getCondition(grid);
        				grid.getCount(caller, condition);
    				};
    				
    			}
    		},
    		'button[id=export]':{
    			beforerender:function(btn){
    				btn.handler = Ext.emptyFn;
    				me.BaseUtil.addListener('click', function(){
    					var grid = Ext.getCmp('grid');
    					var condition = btn.ownerCt.ownerCt.getCondition(grid),
    						_con = grid.getCondition();
    					if(!Ext.isEmpty(_con))
    						condition += ' and (' + _con + ")";
    		    		me.BaseUtil.createExcel(caller, 'datalist', condition, null, null, null, grid);
    				}, btn, 10000);
    			}
    		},
    		'monthdatefield[name=pwm_yearmonth]':{
    			beforerender:function(field){
    				field.autoValue=false;
    				field.fromnow=false;
    				me.getCurrentMonth(field);
    			}
    		},
    		'erpVastDealButton':{
    			click:function(btn){
    				var currentMonth= btn.ownerCt.ownerCt.down('monthdatefield').value;
    				if(!currentMonth) {
    					showError('期间不能为空!') ;
    					return 
    				}else{
    					me.BaseUtil.getActiveTab().setLoading(true);
	    				Ext.Ajax.request({
	    					url:basePath+'scm/product/RefreshProdMonthNew.action',
	    					method:'post',
	    					params:{	
	    						currentMonth:currentMonth
	    					},
	    					timeout: 1200000,
	    					callback : function(options,success,response){
	    						me.BaseUtil.getActiveTab().setLoading(false);
	    		        		var res = new Ext.decode(response.responseText);
	    		        		if(res.exceptionInfo != null){
	    		        			showError(res.exceptionInfo);return;
	    		        		}
	    		        		Ext.Msg.alert("提示", "刷新成功!", function(){
				   					Ext.getCmp('query').handler();
				   				});
	    					}
	    				});
    				}
    			}
    		},
    		'erpTurnPWHMonthAdjustButton':{
    			click:function(btn){
    				var currentMonth= btn.ownerCt.ownerCt.down('monthdatefield').value;
    				if(!currentMonth) {
    					showError('期间不能为空!') ;
    					return 
    				}else{
    					me.BaseUtil.getActiveTab().setLoading(true);
	    				Ext.Ajax.request({
	    					url:basePath+'scm/product/turnProductWHMonthAdjust.action',
	    					method:'post',
	    					params:{	
	    						currentMonth:currentMonth
	    					},
	    					timeout: 1200000,
	    					callback : function(options,success,response){
	    						me.BaseUtil.getActiveTab().setLoading(false);
	    		        		var res = new Ext.decode(response.responseText);
	    		        		if(res.log){
	    		        			showError(res.log);
	    		        		} else {
	    		        			showError(res.exceptionInfo);
	    		        		}
	    		   
	    					}
	    				});
    				}
    			}
    		}
    	});
    },
    getCurrentMonth: function(f) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: 'MONTH-P'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				f.setValue(rs.data.PD_DETNO);
    			}
    		}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	var me = this;
    	if(keyField != null && keyField != ''){//有些datalist不需要打开明细表，这些表在datalist表里面不用配dl_keyField
    		var value = record.data[keyField];
        	var formCondition = keyField + "IS" + value ;
        	var gridCondition = pfField + "IS" + value;
        	var panel = Ext.getCmp(caller + keyField + "=" + value); 
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
    	    	main.getActiveTab().currentStore = me.getCurrentStore(value);//用于单据翻页
    	    	panel = {       
    	    			title : me.BaseUtil.getActiveTab().title+'('+title+')',
    	    			tag : 'iframe',
    	    			tabConfig:{tooltip:me.BaseUtil.getActiveTab().tabConfig.tooltip+'('+keyField + "=" + value+')'},
    	    			frame : true,
    	    			border : false,
    	    			layout : 'fit',
    	    			iconCls : 'x-tree-icon-tab-tab1',
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
    	    	this.openTab(panel, caller + keyField + "=" + record.data[keyField]);
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
});