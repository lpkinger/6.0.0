Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.AccountRegisterPlanPage', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.gs.AccountRegisterPlanPage','core.form.Panel','fa.gs.AccountRegisterTree','common.datalist.GridPanel',
    		'core.button.Add','core.button.Close','core.form.ConDateField','core.button.Query','common.datalist.Toolbar',
    		'core.trigger.DbfindTrigger','core.form.YnField','core.form.BtnDateField'
    	],
    init:function(){
    	var me = this;
    	me.datamanager = [];
    	this.control({
    		'accountregistertree': {
    			itemmousedown: function(selModel, record){
    				me.loadTab(selModel, record);
    				me.lastSelected = record;
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.onClose();
    			}
    		},
    		'erpQueryButton': {
    			click : function(btn) {
    				var grid = Ext.getCmp('grid');
    				var date1=Ext.getCmp('arp_date').firstVal,date2=Ext.getCmp('arp_date').secondVal;
					var condition = "arp_date BETWEEN to_date('" + Ext.Date.toString(date1) + " 00:00:00','yyyy-MM-dd HH24:mi:ss') AND to_date('" 
    				+ Ext.Date.toString(date2) + " 23:59:59','yyyy-MM-dd HH24:mi:ss')";
					grid.getCount(caller, condition);
				}
    		},
    		'erpAddButton':{
    			click: function(btn){
    				me.FormUtil.onAdd('addAccountRegisterPlan', '新增银行预计存取款', 'jsps/common/commonpage.jsp?whoami=AccountRegisterPlan');
    			}
    		},
    		'erpDatalistGridPanel': { 
    			itemclick: this.onGridItemClick,
    			afterrender:function(grid){
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
    		}
    	});
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onGridItemClick: function(selModel, record){//grid行选择
	    var me = this;
	    if(keyField != null && keyField != ''){//有些datalist不需要打开明细表，这些表在datalist表里面不用配dl_keyField
	    	var value = record.data[keyField];
	        var formCondition = keyField + "IS" + value ;
	        var gridCondition = pfField + "IS" + value;
	        if(!Ext.isEmpty(pfField) && pfField.indexOf('+') > -1) {//多条件传入//vd_vsid@vd_id+vd_class@vd_class
	        	var arr = pfField.split('+'),ff = [],k = [];
	        	Ext.Array.each(arr, function(r){
	        		ff = r.split('@');
	        		k.push(ff[0] + 'IS\'' + record.get(ff[1]) + '\'');
	        	});
	        	gridCondition = k.join(' AND ');
	        }
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
	    	    main.getActiveTab().currentStore = me.getCurrentStore(value);//用于单据翻页
	    	    if(main._mobile) {
	    	    	main.addPanel(me.BaseUtil.getActiveTab().title+'('+title+')', myurl, panelId);
	    	    } else {
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
	        	   this.openTab(panel, panelId);
	    	   }
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
    	var me = this;
    	var tree = Ext.getCmp('tree-panel');
    	if (!record.get('leaf')) {
    		if(record.isExpanded() && record.childNodes.length > 0){//是根节点，且已展开
				record.collapse(true,true);//收拢
			} else {//未展开
				//看是否加载了其children
				if(record.childNodes.length == 0){
					//从后台加载
		            tree.setLoading(true, tree.body);
					Ext.Ajax.request({//拿到tree数据
			        	url : basePath + 'fa/gs/getCategoryBankTree.action',
			        	params: {
			        		parentid: record.data['id']
			        	},
			        	async: false,
			        	callback : function(options,success,response){
			        		tree.setLoading(false);
			        		var res = new Ext.decode(response.responseText);
			        		if(res.tree){
			        			record.appendChild(res.tree);
			        			record.expand(false,true);//展开
			        		} else if(res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        		}
			        	}
			        });
				} else {
					record.expand(false,true);//展开
				}
			}
    	} else {
    		condition = 'arp_cateid='+record.get('id');
    		Ext.getCmp('grid').getCount(caller,condition);
    	}
    	tree.getExpandedItems(record);
    }
});