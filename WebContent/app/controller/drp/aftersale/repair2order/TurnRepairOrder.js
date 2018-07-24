Ext.QuickTips.init();
Ext.define('erp.controller.drp.aftersale.repair2order.TurnRepairOrder', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
    views:[
     		'drp.aftersale.repair2order.Viewport','drp.aftersale.repair2order.GridPanel','drp.aftersale.repair2order.Toolbar','core.button.VastAudit','core.button.VastDelete',
     		'core.button.VastPrint','core.button.VastReply','core.button.VastSubmit','core.button.ResAudit','core.form.FtField',
     		'core.grid.TfColumn','core.grid.YnColumn','core.trigger.DbfindTrigger','core.form.FtDateField','core.form.FtFindField',
     		'core.form.FtNumberField'
     	],
    init:function(){
        this.BaseUtil = Ext.create('erp.util.BaseUtil');
        this.FormUtil = Ext.create('erp.util.FormUtil');
    	this.control({
    		'erpRepair2OrderGridPanel': {
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
    		},
    		'erpVastDeleteButton': {
    			click: function(btn){
    				var dlwin = new Ext.window.Window({
   			    		id : 'dlwin',
	   				    title: btn.text,
	   				    height: "100%",
	   				    width: "80%",
	   				    maximizable : true,
	   					buttonAlign : 'center',
	   					layout : 'anchor',
	   				    items: [{
	   				    	  tag : 'iframe',
	   				    	  frame : true,
	   				    	  anchor : '100% 100%',
	   				    	  layout : 'fit',
	   				    	  html : '<iframe id="iframe_dl_'+caller+'" src="'+basePath+'jsps/common/vastDatalist.jsp?urlcondition='+condition+'&whoami='+caller+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
	   				    }],
	   				    buttons : [{
	   				    	text: btn.text,
	   				    	iconCls: btn.iconCls,
	   				    	cls: 'x-btn-gray-1',
	   				    	handler: function(){
	   				    		
	   				    	}
	   				    },{
	   				    	text : '关  闭',
	   				    	iconCls: 'x-button-icon-close',
	   				    	cls: 'x-btn-gray',
	   				    	handler : function(){
	   				    		Ext.getCmp('dlwin').close();
	   				    	}
	   				    }]
	   				});
	   				dlwin.show();
    			}
    		},
    		'button[id=searchlist]': {
    			click: function(){
    				this.showSearchListWin();
    			}
    		},
    		'dbfindtrigger[name=sl_label]': {
    			afterrender: function(t){
    				t.dbBaseCondition = 'sl_caller=\'' + caller + '\'';
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
    showSearchListWin: function(){
    	var me = this;
    	if(!Ext.getCmp('slwin')){
    		Ext.create('Ext.window.Window', {
        		id : 'slwin',
        		title: '高级查询',
        		height: screen.height*0.7*0.8,
        		width: screen.width*0.7*0.6,
        		maximizable : true,
        		closable: false,
        		buttonAlign : 'center',
        		layout : 'border',
        		bodyStyle: 'background:#f1f1f1;',
        		tools: [{
        			type: 'close',
        			handler: function(){
        				Ext.getCmp('slwin').down('grid').setEffectData();//保留已选择的条件
        				Ext.getCmp('slwin').hide();
        			}
        		}],
        		items: [{
        			xtype: 'form',
        			region: 'north',
        			layout: 'column',
        			bodyStyle: 'background:#f1f1f1;',
        			title: '已选择条件',
        			maxHeight: 100,
        			items: me.getFilterCondition(),
        			buttonAlign: 'center',
        			buttons: [{
        				name: 'query',
        				id: 'query',
        				text: $I18N.common.button.erpQueryButton,
        				iconCls: 'x-button-icon-query',
        		    	cls: 'x-btn-gray',
        		    	handler: function(btn){
        		    		var con = btn.ownerCt.ownerCt.ownerCt.down('grid').getCondition();//保留已选择的条件
        		    		Ext.getCmp('grid').getCount(caller, con);
    			    		btn.ownerCt.ownerCt.ownerCt.hide();
        		    	}
        			},{
        				cls: 'x-btn-gray',
    			    	text: $I18N.common.button.erpOffButton,
    			    	handler: function(btn){
    			    		btn.ownerCt.ownerCt.ownerCt.down('grid').setEffectData();
    			    		btn.ownerCt.ownerCt.ownerCt.hide();
    			    	}
        			},{
        				xtype: 'radio',
        				name: 'separator',
        				boxLabel: '与',
        				checked: true,
        				inputValue: 'AND',
        				getCheckValue: function(){
        					return this.checked ? 'AND' : 'OR';
        				}
        			},{
        				xtype: 'radio',
        				name: 'separator',
        				boxLabel: '或',
        				inputValue: 'OR'
        			}]
        		}, me.getSearchListGrid()]
        	});
    	}
    	Ext.getCmp('slwin').show();
    	Ext.getCmp('slwin').down('grid').loadData();
    },
    getFilterCondition: function(){
    	var fields = Ext.getCmp('grid').plugins[0].fields;
    	var items = new Array();
    	Ext.each(Ext.Object.getKeys(fields), function(key){
    		var item = fields[key];
    		if(item.value != null && item.value.toString().trim() != ''){
    			items.push({
    				xtype: item.xtype,
    				id: item.itemId,
    				fieldLabel: item.fieldLabel,
    				fieldStyle: item.fieldStyle,
    				value: item.value,
    				columnWidth: 0.5,
    				cls: 'form-field-border',
    				listeners: {
    					change: function(f){
    						Ext.getCmp(item.id).setValue(f.value);
    					}
    				}
    			});
    		}
    	});
    	return items;
    },
    getSearchListGrid: function(){
    	var grid = Ext.create('Ext.grid.Panel', {
    		minHeight: 300,
    		region: 'center',
    		store: Ext.create('Ext.data.Store', {
    			 fields:[{
    				 name: 'sl_label',
    				 type: 'string'
    			 },{
    				 name: 'sl_field',
    				 type: 'string'
    			 },{
    				 name: 'sl_type',
    				 type: 'string'
    			 },{
    				 name: 'union',
    				 type: 'string'
    			 },{
    				 name: 'value'
    			 }],
    			 data: []
    		}),
    		columns: [{
    			text: '条件',
    			flex: 2,
    			dataIndex: 'sl_label',
    			editor: {
    				xtype: 'dbfindtrigger'
    			},
    			dbfind: 'SearchList|sl_label'
    		},{
    			text: '',
    			hidden: true,
    			dataIndex: 'sl_field'
    		},{
    			text: '',
    			hidden: true,
    			dataIndex: 'sl_type'
    		},{
    			text: '关系',
    			flex: 1,
    			dataIndex: 'union',
    			editor: {
    				xtype: 'combo',
    				store: Ext.create('Ext.data.Store', {
    	                fields: ['display', 'value'],
    	                data : [
    	                    {"display": 'Like', "value": 'like'},
    	                    {"display": '等于', "value": '='},
    	                    {"display": '大于', "value": '>'},
    	                    {"display": '小于', "value": '<'},
    	                    {"display": '不等于', "value": '<>'},
    	                    {"display": '介于', "value": 'Between And'}
    	                ]
    	            }),
    	            displayField: 'display',
    	            valueField: 'value',
    	    		queryMode: 'local',
    	    		editable: false,
    	    		value: 'like'
    			}
    		},{
    			text: '值',
    			flex: 3,
    			dataIndex: 'value',
    			renderer: function(val){
    				if(Ext.isDate(val)){
    					return Ext.Date.format(val, 'Y-m-d');
    				}
    				return val;
    			}
    		}],
    		columnLines: true,
    		plugins: Ext.create('Ext.grid.plugin.CellEditing', {
    			clicksToEdit: 1,
    			listeners: {
    				beforeedit: function(e){
    					if(e.field == 'value'){
    	        			var record = e.record;
    						var column = e.column;
    						if(record.data['union'] == null || record.data['union'] == ''){
								record.set('union', '=');
							}
    						var f = record.data['sl_field'];
    						switch(record.data['sl_type']){
	    						case 'D':
	    							switch(record.data['union']){
		    							case 'Between And':
		    								column.setEditor(new erp.view.core.form.FtDateField({
		    									id: f,
		    									name: f
		    								}));break;
		    							default:
		    								column.setEditor(new Ext.form.field.Date({
		    									id: f,
		    									name: f
		    								}));break;
	    							}
	    							break;
	    						case 'S':
	    							switch(record.data['union']){
		    							case 'Between And':
		    								column.setEditor(new erp.view.core.form.FtField({
		    									id: f,
		    									name: f,
		    									value: e.value
		    								}));break;
		    							default:
		    								column.setEditor(new Ext.form.field.Text({
		    									id: f,
		    									name: f
		    								}));break;
	    							}
	    							break;
	    						case 'N':
	    							switch(record.data['union']){
		    							case 'Between And':
		    								column.setEditor(new erp.view.core.form.FtNumberField({
		    									id: f,
		    									name: f
		    								}));break;
		    							default:
		    								column.setEditor(new Ext.form.field.Number({
		    									id: f,
		    									name: f
		    								}));break;
	    							}
	    							break;
	    						case 'T':
	    							column.dbfind = 'a|b';
	    							switch(record.data['union']){
		    							case 'Between And':
		    								column.setEditor(new erp.view.core.form.FtFindField({
		    									id: f,
		    									name: f
		    								}));break;
		    							default:
		    								column.setEditor(new erp.view.core.trigger.DbfindTrigger({
		    									id: f,
		    									name: f
		    								}));break;
	    							}
	    							break;
	    						default:
	    							column.setEditor(null);
    						}
    					}
    				}
    			}
    		}),
    		dbfinds: [{
    	    	field: 'sl_label',
    	    	dbGridField: 'sl_label'
    	    },{
    	    	field: 'sl_field',
    	    	dbGridField: 'sl_field'
    	    },{
    	    	field: 'sl_type',
    	    	dbGridField: 'sl_type'
    	    }],
    	    selModel: Ext.create('Ext.selection.CheckboxModel',{
    	    	
    		}),
    		setEffectData: function(){
    			var me = this;
    			var datas = new Array();
    			Ext.each(me.selModel.getSelection(), function(item){
    				var data = item.data;
    				if(!Ext.isEmpty(data.sl_label) && !Ext.isEmpty(data.union) && !Ext.isEmpty(data.value)){
    					datas.push(data);
    				}
    			});
    			me.effectdata = datas;
    		},
    		getEffectData: function(){
    			return this.effectdata || new Array();
    		},
    		loadData: function(){
    			var data = this.getEffectData();
    			this.store.loadData(data);
    			this.selModel.selectAll();
    			this.store.add([{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}]);
    		},
    		/**
    		 * 将数据拼成Sql条件语句
    		 */
    		getCondition: function(){
    			this.setEffectData();
    			var condition = '';
    			var separator = this.up('window').down('form').down('radio').getCheckValue();
    			Ext.each(this.effectdata, function(data){
    				if(data.union == 'Between And'){
    					var v1 = data.value.split('~')[0];
    					var v2 = data.value.split('~')[1];
    					if(data.sl_type == 'D'){
    						if(condition == ''){
    							condition = '(' + data.sl_field + " BETWEEN to_date('" + v1 + " 00:00:00','yyyy-MM-dd HH24:mi:ss') AND to_date('" 
	        					+ v2 + " 23:59:59','yyyy-MM-dd HH24:mi:ss')" + ') ';
    						} else {
    							condition += ' ' + separator + ' (' + data.sl_field + " BETWEEN to_date('" + v1 + " 00:00:00','yyyy-MM-dd HH24:mi:ss') AND to_date('" 
    	        					+ v2 + " 23:59:59','yyyy-MM-dd HH24:mi:ss')" + ') ';
    						}
    					} else if(data.sl_type == 'N'){
    						if(condition == ''){
    							condition = '(' + data.sl_field + " BETWEEN " + v1 + ' AND ' + v2 + ') ';
    						} else {
    							condition += ' ' + separator + ' (' + data.sl_field + " BETWEEN " + v1 + ' AND ' + v2 + ') ';
    						}
    					} else{
    						if(condition == ''){
    							condition = '(' + data.sl_field + " BETWEEN '" + v1 + "' AND '" + v2 + "') ";
    						} else {
    							condition += ' ' + separator + ' (' + data.sl_field + " BETWEEN '" + v1 + "' AND '" + v2 + "') ";
    						}
    					}
    				} else {
    					if(data.sl_type == 'D'){
    						var v = data.value;
    						if(data.union == '<'){
								v = "to_date('" + v + " 00:00:00','yyyy-MM-dd HH24:mi:ss')";
							} else if(data.union == '>'){
								v = "to_date('" + v + " 23:59:59','yyyy-MM-dd HH24:mi:ss')";
							} else {
								v = "to_date('" + v + "','yyyy-MM-dd')";
							}
    						if(condition == ''){
    							condition = '(' + data.sl_field + data.union + v + ') ';
    						} else {
    							condition += ' ' + separator +' (' + data.sl_field + data.union + v + ') ';
    						}
    					} else {
    						var v = data.value;
    						if(data.union == 'like'){
    							v = " '%" + data.value + "%'";
    						} else {
    							v = " '" + data.value + "'";
    						}
    						if(condition == ''){
    							condition = '(' + data.sl_field + " " + data.union + v + ") ";
    						} else {
    							condition += ' ' + separator +' (' + data.sl_field + " " + data.union + v + ") ";
    						}
    					}
    				}
    			});
    			return condition;
    		}
    	});
    	return grid;
    }
});