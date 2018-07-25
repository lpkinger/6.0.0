Ext.QuickTips.init();
Ext.define('erp.controller.common.QueryDetail', {
	extend : 'Ext.app.Controller',
	requires: ['erp.util.BaseUtil'],
	views : [ 'common.queryDetail.Viewport', 'common.datalist.GridPanel', 'common.query.Form', 'core.trigger.DbfindTrigger',
			'core.form.FtField', 'core.form.ConDateField', 'core.form.YnField', 'core.form.FtDateField','common.datalist.Toolbar',
			'core.form.MonthDateField','core.form.FtFindField', 'core.grid.YnColumn', 'core.grid.TfColumn', 
			'core.form.ConMonthDateField','core.form.BtnDateField'],
	init : function() {
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.control({
			'erpQueryFormPanel button[id=query]' : {
				click : function(btn) {
					var grid = Ext.getCmp('grid');
					var form = btn.ownerCt.ownerCt;
					var urlcondition = grid.defaultCondition || '';
					condition = form.spellCondition(urlcondition);
					grid.formCondition = condition;
					grid.getCount(null, condition);
				}
			},
			'erpQueryFormPanel': {
				afterrender: function(f) {
					f.down('button[name=query]').handler = Ext.emptyFn;
					f.down('button[name=export]').handler = function(btn){
			    		var grid = Ext.getCmp('grid');
			    		var condition = grid.getCondition() || '';
		    			condition = f.spellCondition(condition);
		    			if(Ext.isEmpty(condition)) {
		    				condition = '1=1';
		    			}
			    		grid.BaseUtil.createExcel(caller, 'datalist', condition, null, null, null, grid);
			    	};
				}
			},
			'button[id=searchlist]': {
			   click: function(){
				   this.showSearchListWin();
			   }
	    	},
		    'button[id=customize]': {
			   click: function(){
				   this.showCustomizeWin();
			   }
		    },
			'erpDatalistGridPanel' : {
				itemclick : this.onGridItemClick,
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
			'monthdatefield': {
				afterrender: function(f) {
					var type = '';
					if(f.name == 'cd_yearmonth') {
						type = 'MONTH-T';
					}
					if(f.name == 'cmc_yearmonth') {
						type = 'MONTH-A';
					}
					if(f.name == 'cm_yearmonth') {
						type = 'MONTH-A';
					}
					if(f.name == 'am_yearmonth') {
						type = 'MONTH-B';
					}
					if(type != '') {
						this.getCurrentMonth(f, type);
					}
				}
			},
			'conmonthdatefield': {
				afterrender: function(f) {
					var type = '';
					if(f.name == 'cd_yearmonth') {
						type = 'MONTH-T';
					}
					if(f.name == 'cmc_yearmonth') {
						type = 'MONTH-A';
					}
					if(f.name == 'cm_yearmonth') {
						type = 'MONTH-A';
					}
					if(f.name == 'am_yearmonth') {
						type = 'MONTH-B';
					}
					if(type != '' && Ext.isEmpty(getUrlParam(f.name))) {
						this.getCurrentMonth(f, type);
					}
				}
			},
			'button[name=refresh]':{
    			click: function(btn){   
    				var form = btn.ownerCt.ownerCt;
    				if (caller == 'ALMonth!Query'){
    					 var month = Ext.getCmp('am_yearmonth');
    					 var condition=month.secondVal;
    					 Ext.Ajax.request({
        					url: basePath + 'fa/ars/CmQueryController/refreshQuery.action',
        					params: {
        						condition:condition
    						},
        					method: 'post',        					
        					callback: function(opt, s, r) {        						
        						var rs = Ext.decode(r.responseText);
        						if(rs.success) {
        								form.onQuery();        							
        						}else{
        							
        						}
        					}
        				});
    				} else if (caller == 'Make!Cost!Query'){
    					Ext.Msg.alert("提示","请到成本计算界面进行成本计算！");
    				}else{
    					form.onQuery();
    				}
    			}
			}
		});
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	if(keyField != null && keyField != ''){//有些datalist不需要打开明细表，这些表在datalist表里面不用配dl_keyField
    		if(keyField.indexOf('+') > 0) {//多条件传入查询界面//vd_vsid@vd_id+vd_class@vd_class
    			this.openQueryUrl(record);
    		} else {
    			this.openUrl(record);
    		}
    	}
    }, 
    openUrl: function(record) {
    	var me = this, value = record.data[keyField];
    	var formCondition = keyField + "IS" + value ;
    	var gridCondition = pfField + "IS" + value;
    	if(!Ext.isEmpty(pfField) && pfField.indexOf('+') > -1) {//多条件传入维护界面//vd_vsid@vd_id+vd_class@vd_class
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
    },
    openQueryUrl: function(record) {
    	var me = this, arr = keyField.split('+'),ff = [],k = [];//vd_vsid@vd_id+vd_class@vd_class
		Ext.Array.each(arr, function(r){
			ff = r.split('@');
			k.push(ff[0] + '=' + record.get(ff[1]));
		});
		var myurl = k.join('&');
		var panelId = caller +  "_" + myurl;
    	var panel = Ext.getCmp(panelId); 
    	var main = parent.Ext.getCmp("content-panel");
    	if(!main){
			main = parent.parent.Ext.getCmp("content-panel");
		}
    	if(!panel){ 
    		var title = me.BaseUtil.getActiveTab().title + '-查询';
    		if(contains(url, '?', true)){
    			myurl = url + '&' + myurl;
    		} else {
    			myurl = url + '?' + myurl;
    		}
	    	if (main._mobile) {
	    		main.addPanel(title, myurl, panelId);
	    	} else {
	    		panel = {       
    	    			title : title,
    	    			tag : 'iframe',
    	    			tabConfig: {tooltip: title},
    	    			frame : true,
    	    			border : false,
    	    			layout : 'fit',
    	    			iconCls : 'x-tree-icon-tab-tab1',
    	    			html : '<iframe src="' + myurl + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
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
    	} else { 
	    	main.setActiveTab(panel); 
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
    getCurrentMonth: function(f, type) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			type: type
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				f.setValue(rs.data.PD_DETNO);
    			}
    		}
    	});
    },
    showSearchListWin: function(){
	   var me = this, win = this.searchWin;
	   if (!win){
		   win = this.searchWin = Ext.create('Ext.window.Window', {
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
				   handler: function(e, el, header, tool){
					   tool.ownerCt.ownerCt.down('grid').setEffectData();//保留已选择的条件
					   tool.ownerCt.ownerCt.hide();
				   }
			   }],
			   items: [{
				   xtype: 'form',
				   region: 'north',
				   layout: 'column',
				   bodyStyle: 'background:#f1f1f1;',
				   maxHeight: 100,
				   buttonAlign: 'center',
				   buttons: [{
					   name: 'query',
					   id: 'query',
					   text: $I18N.common.button.erpQueryButton,
					   iconCls: 'x-button-icon-query',
					   cls: 'x-btn-gray',
					   handler: function(btn){
						   Ext.getCmp('grid').getCount(caller);
						   btn.ownerCt.ownerCt.ownerCt.hide();
					   }
				   },{
					   cls: 'x-btn-gray',
					   text: '清空',
					   handler: function(btn){
						   btn.ownerCt.ownerCt.ownerCt.down('grid').store.loadData([{},{},{},{},{},{},{},{},{},{}]);
						   Ext.getCmp('grid').getCount(caller);
					   }
				   },{
					   cls: 'x-btn-gray',
					   text: '关闭',
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
			   }, me.getSearchListGrid(), me.getTemplateForm() ]
		   });
		   Ext.getCmp('grid').searchGrid = win.down('grid');
		   Ext.getCmp('grid').tempalteForm = win.down('form[name=template]');
		   this.getTemplates(caller);
	   }
	   win.show();
	   win.down('grid').loadData();
   },
   getSearchListGrid: function(){
	   var data = this.getGridColumns();
	   var grid = Ext.create('Ext.grid.Panel', {
		   maxHeight: 350,
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
				   name: 'sl_dbfind',
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
				   xtype: 'combo',
				   store : Ext.create('Ext.data.Store', {
					   fields : [ 'display', 'value', 'column' ],
					   data : data
				   }),
				   editable: false,
				   displayField : 'display',
				   valueField : 'value',
				   queryMode : 'local'
			   },
			   renderer : function(val, meta, record, x, y, store, view) {
				   if (val) {
					   var column = view.ownerCt.headerCt.getHeaderAtIndex(y);
					   if(column && typeof column.getEditor != 'undefined') {
						   var	editor = column.getEditor(record);
						   if (editor && editor.lastSelection.length > 0) {
							   var cm = editor.lastSelection[0].get('column'),
							   field = cm.dataIndex;
							   if (record.get('sl_field') != field)
								   record.set('sl_field', field);
							   var t = 'S';
							   if(cm.xtype == 'datecolumn' || cm.xtype == 'datetimecolumn') {
								   t = 'D';
							   } else if(cm.xtype == 'numbercolumn') {
								   t = 'N';
							   }
							   if (record.get('sl_type') != t)
								   record.set('sl_type', t);
						   }
					   }
				   } else {
					   if (record.get('sl_field')) {
						   record.set('sl_field', null);
					   }
				   }
				   return val;
			   },
			   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
				   if (type == 'click' || type == 'dbclick') {
					   return true;
				   }
				   return false;
			   }
		   },{
			   text: '',
			   hidden: true,
			   dataIndex: 'sl_field',
			   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
				   return false;
			   }
		   },{
			   text: '',
			   hidden: true,
			   dataIndex: 'sl_type',
			   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
				   return false;
			   }
		   },{
			   text: '关系',
			   flex: 1,
			   dataIndex: 'union',
			   xtype:'combocolumn',
			   editor: {
				   xtype: 'combo',
				   store: Ext.create('Ext.data.Store', {
					   fields: ['display', 'value'],
					   data : [{"display": '等于', "value": '='},
					           {"display": '大于', "value": '>'},
					           {"display": '大于等于', "value": '>='},
					           {"display": '小于', "value": '<'},
					           {"display": '小于等于', "value": '<='},
					           {"display": '不等于', "value": '<>'},
					           {"display": '介于', "value": 'Between And'},
					           {"display": '包含', "value": 'like'},
					           {"display": '不包含', "value": 'not like'},
					           {"display": '开头是', "value": 'begin like'},
					           {"display": '开头不是', "value": 'begin not like'},
					           {"display": '结尾是', "value": 'end like'},
					           {"display": '结尾不是', "value": 'end not like'}]
				   }),
				   displayField: 'display',
				   valueField: 'value',
				   queryMode: 'local',
				   editable: false,
				   value: 'like'
			   },
			  /* renderer : function(v) {
				   var r = v;
				   switch(v) {
				   case 'like':
					   r = 'Like';break;
				   case '=':
					   r = '等于';break;
				   case '>':
					   r = '大于';break;
				   case '>=':
					   r = '大于等于';break;
				   case '<':
					   r = '小于';break;
				   case '<=':
					   r = '小于等于';break;
				   case '<>':
					   r = '不等于';break;
				   case 'Between And':
					   r = '介于';break;
				   }
				   return r;
			   },*/
			   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
				   if (type == 'click' || type == 'dbclick') {
					   return true;
				   }
				   return false;
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
			   },
			   processEvent : function(type, view, cell, recordIndex, cellIndex, e) {
				   if (type == 'click' || type == 'dbclick') {
					   var s = view.ownerCt.selModel, m = s.getSelection(), n = [];
					   Ext.Array.each(m, function(){
						   n.push(this);
					   });
					   n.push(view.ownerCt.store.getAt(recordIndex));
					   s.select(n);
					   return true;
				   }
				   return false;
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
							   record.set('union', 'like');
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
							   column.dbfind = record.get('sl_dbfind');
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
			   if(!this.effectdata) {
				   this.store.add([{},{},{},{},{},{},{},{},{},{}]);
			   }
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
						   var v = data.value, field = data.sl_field;
						   if(Ext.isDate(v)) {
							   v = Ext.Date.format(v, 'Y-m-d');
						   }
						   if(data.union == '<' || data.union == '<=' || data.union == '>' || data.union == '>='){
							   v = "to_date('" + v + "','yyyy-MM-dd')";
						   }else {
							   v = Ext.Date.format(data.value, 'Ymd');
							   field = "to_char(" + field + ",'yyyymmdd')";
						   }
						   if(condition == ''){
							   condition = '(' + field + data.union + v + ') ';
						   } else {
							   condition += ' ' + separator +' (' + field + data.union + v + ') ';
						   }
					   } else {
						   var v = data.value;
						   var u =data.union ;
						   if(data.union == 'like' || data.union=='not like'){
							   v = " '%" + data.value + "%'";
						   }else if(data.union =='begin like' || data.union =='begin not like'){
							   v = " '" + data.value + "%'";
							   u=data.union.substring(5);
						   }else if(data.union =='end like' || data.union=='end not like'){
							   v = " '%" + data.value + "'";
							   u=data.union.substring(3);
						   }else {
							   v = " '" + data.value + "'";
						   }
						   if(condition == ''){
							   condition = '(' + data.sl_field + " " +u + v + ") ";
						   } else {
							   condition += ' ' + separator +' (' + data.sl_field + " " +u  + v + ") ";
						   }
					   }
				   }
			   });
			   return condition;
		   }
	   });
	   return grid;
   },
   getTemplateForm : function() {
	   var me = this;
	   return Ext.create('Ext.form.Panel', {
		   minHeight : 150,
		   region : 'south',
		   layout : 'column',
		   name : 'template',
		   bodyStyle : 'background:#f1f2f5;',
		   items : [{
			   xtype : 'fieldcontainer',
			   columnWidth : .75,
			   defaults : {
				   xtype : 'radio',
				   flex : 1,
				   margin : '5 0 5 15',
				   labelAlign : 'right'
			   },
			   items : [{
				   name : 'export-type',
				   boxLabel : '全部列',
				   checked : true
			   }]
		   }, {
			   xtype : 'fieldcontainer',
			   columnWidth : .25,
			   items : [{
				   xtype : 'button',
				   cls : 'x-btn-gray',
				   iconCls : 'x-button-icon-excel',
				   flex : 1,
				   margin : '15 0 5 0',
				   width : 80,
				   text : '导&nbsp;&nbsp;&nbsp;出',
				   handler : function(b) {
					   var grid = Ext.getCmp('grid'),
					   tb = grid.down('erpDatalistToolbar'),
					   r = b.ownerCt.ownerCt.down('radio[checked=true]');
					   if (r.fields)
						   tb.exportData(grid, b, r.boxLabel, r.fields);
					   else
						   tb.exportData(grid, b);
				   }
			   }
			   //已存在个性化设置列功能
			   /*,{
				   xtype : 'button',
				   cls : 'x-btn-gray',
				   flex : 1,
				   margin : '5 0 10 0',
				   width : 80,
				   text : '新建模板',
				   handler : function(b, e) {
					   var grid = Ext.getCmp('grid');
					   me.addTemplate(grid);
				   }
			   }*/
			   ]
		   }]
	   });
   },
   getGridColumns : function() {
	   var grid = Ext.getCmp('grid'), columns = grid.headerCt.getGridColumns(), data = [];
	   Ext.each(columns, function(){
		   if(this.dataIndex && this.getWidth() > 0) {
			   data.push({
				   display : this.text,
				   value : this.text,
				   column : this
			   });
		   }
	   });
	   return data;
   },
   getTemplates : function(caller) {
	   Ext.Ajax.request({
		   url : basePath + 'common/getFieldsDatas.action',
		   async: false,
		   params: {
			   caller: 'DataTemplate',
			   fields: 'dt_desc,dt_fields',
			   condition: 'dt_caller=\'' + caller + '\''
		   },
		   method : 'post',
		   callback : function(opt, s, res){
			   var r = new Ext.decode(res.responseText);
			   if(r.exceptionInfo){
				   showError(r.exceptionInfo);return;
			   }
			   if (r.success && r.data) {
				   var f = [], dd = Ext.decode(r.data);
				   if(dd.length > 0) {
					   Ext.each(dd, function(){
						   f.push({
							   name : 'export-type',
							   boxLabel : this.DT_DESC,
							   fields : this.DT_FIELDS
						   });
					   });
					   var form = Ext.getCmp('grid').tempalteForm;
					   form.items.first().add(f);
				   }
			   }
		   }
	   });
   },
   showCustomizeWin:function(){
	   var me = this, win = this.CustomizeWin, grid = Ext.getCmp('grid');
	   if(!win){
		   var ablecolumns=new Array(),unselectcolumns=grid.basecolumns;
		   Ext.Array.each(grid.columns,function(item){
			   if(item.text && item.text.indexOf('&#160')<0){
				   ablecolumns.push(item);
			   }
	   });
	   unselectcolumns.splice(0,ablecolumns.length);
	   this.CustomizeWin=win = Ext.create('Ext.window.Window', {
		   title: '<div align="center">个性设置</div>',
		   height: screen.height*0.7,
		   width: screen.width*0.7*0.9,
		   layout:'border',
		   closeAction:'hide',
		   items:[{
			   region:'center',
			   layout:{
				   type: 'hbox',
				   align: 'stretch',
				   padding: 5
			   },
			   defaults     : { flex : 1 },
			   items:[{
				   xtype:'grid',
				   multiSelect: true,
				   id: 'fromgrid',
				   title:'不显示列',
				   flex:0.7,
				   cls: 'custom-grid',	    		
				   store:Ext.create('Ext.data.Store', {
					   fields: [{name:'fullName',type:'string'},{name:'text',type:'string'},{name:'width',type:'number'}],
					   data: unselectcolumns,
					   filterOnLoad: false 
				   }),
				   plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
				   viewConfig: {
					   plugins: {
						   ptype: 'gridviewdragdrop',
						   dragGroup: 'togrid',
						   dropGroup: 'togrid'
					   }	    					
				   },
				   stripeRows: false,
				   columnLines:true,
				   columns:[{
					   dataIndex:'fullName',
					   cls :"x-grid-header-1",
					   text:'字段名称',
					   width:120,
					   filter: {
						   xtype : 'textfield'
					   }
				   },{
					   dataIndex:'text',
					   text:'描述',
					   cls :"x-grid-header-1",
					   flex:1,
					   filter: {
						   xtype : 'textfield'
					   }
				   },{
					   dataIndex:'width',
					   text:'宽度',
					   width:60,
					   cls :"x-grid-header-1",
					   align:'right',
					   editor: {
						   xtype: 'numberfield',
						   format:0
					   },
					   filter: {
						   xtype : 'textfield'
					   }
				   }]
			   },{
				   xtype:'grid',
				   multiSelect: true,
				   id: 'togrid',
				   stripeRows: true,
				   columnLines:true,
				   title:'显示列',
				   store:Ext.create('Ext.data.Store', {
					   fields: [{name:'fullName',type:'string'},{name:'text',type:'string'},{name:'width',type:'number'},
					            {name:'orderby',type:'string'},{name:'priority',type:'string'}],
					            data:ablecolumns,
					            filterOnLoad: false 
				   }),
				   necessaryField:'fullName',
				   plugins: [Ext.create('erp.view.core.grid.HeaderFilter'),
				             Ext.create('Ext.grid.plugin.CellEditing', {
				            	 clicksToEdit: 1
				             })],
				             viewConfig: {
				            	 plugins: {
				            		 ptype: 'gridviewdragdrop',
				            		 dragGroup: 'togrid',
				            		 dropGroup: 'togrid'
				            	 }
				             },
				             columns:[{
				            	 dataIndex:'fullName',
				            	 text:'字段名称',
				            	 cls :"x-grid-header-1",
				            	 width:120,
				            	 filter: {
				            		 xtype : 'textfield'
				            	 }
				             },{
				            	 dataIndex:'text',
				            	 text:'描述',
				            	 cls :"x-grid-header-1",
				            	 flex:1,
				            	 filter: {
				            		 xtype : 'textfield'
				            	 }
				             },{
				            	 dataIndex:'width',
				            	 text:'宽度',
				            	 width:60,
				            	 xtype:'numbercolumn',
				            	 align:'right',
				            	 cls :"x-grid-header-1",
				            	 filter: {
				            		 xtype : 'textfield'
				            	 },
				            	 editable:true,
				            	 format: '0',
				            	 editor: {
				            		 xtype: 'numberfield',
				            		 hideTrigger: true
				            	 }
				             },
				             {
				            	 dataIndex:'orderby',
				            	 text:'排序',
				            	 width:60,
				            	 xtype:'combocolumn',
				            	 cls :"x-grid-header-1",
				            	 filter: {
				            		 xtype : 'textfield'
				            	 },
				            	 renderer:function(val){
				            		 if(val=='ASC'){
				            			 return '<img src="' + basePath + 'resource/images/16/up.png">' + 
				            			 '<span style="color:red;padding-left:2px">升序</span>';
				            		 } else if(val=='DESC') {
				            			 return '<img src="' + basePath + 'resource/images/16/down.png">' + 
				            			 '<span style="color:red;padding-left:2px">降序</span>';
				            		 }
				            	 },
				            	editor:{
				            			 xtype:'combo',
				            			 queryMode: 'local',
				            			 displayField: 'display',
				            			 valueField: 'value',
				            			 store:Ext.create('Ext.data.Store', {
				            				 fields: ['value', 'display'],
				            				 data : [{value:"ASC", display:"升序"},
				            				         {value:"DESC", display:"降序"}]
				            			 })
				            		 }
				            	 },{
				            		 dataIndex:'priority',
				            		 text:'优先级',
				            		 width:60,
				            		 align:'right',
				            		 cls :"x-grid-header-1",
				            		 filter: {
				            			 xtype : 'textfield'
				            		 },
				            		 editor:{
				            			 xtype:'combo',
				            			 queryMode: 'local',
				            			 displayField: 'display',
				            			 valueField: 'value',
				            			 store:Ext.create('Ext.data.Store', {
				            				 fields: ['value', 'display'],
				            				 data : [{value:"1", display:"1"},
				            				         {value:"2", display:"2"},
				            				         {value:"3", display:"3"},
				            				         {value:"4", display:"4"},
				            				         {value:"5", display:"5"},
				            				         {value:"6", display:"6"},
				            				         {value:"7", display:"7"},
				            				         {value:"8", display:"8"},
				            				         {value:"9", display:"9"}]
				            			 })
				            		 }
				            	 }] 
				             }]
			   }],
			   buttonAlign:'left',
			   buttons:[{xtype : 'tbtext',
				   text:'<font color=gray>*【提示】拖动显示列可设置列是否显示及显示顺序</font>' },'->',{
				   text:'重置',
				   scope:this,
				   handler:function(btn){
					   warnMsg('重置列表将还原配置，确认重置吗?', function(btn){
						if(btn == 'yes'){
							Ext.Ajax.request({
								url : basePath + 'common/resetEmpsDataListDetails.action?_noc=1',
								params: {
									caller:caller
								},
								method : 'post',
								callback : function(options,success,response){
									var localJson = new Ext.decode(response.responseText);
									if(localJson.success){
										showMessage('提示','重置成功!',1000);
											window.location.reload();
									} else {
										if(localJson.exceptionInfo){
											var str = localJson.exceptionInfo;
											if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
												str = str.replace('AFTERSUCCESS', '');
												showError(str);
											} else {
												showError(str);return;
											}
										}
									}
								}
							});
						 }
					  });
				   }
			    },{
				   style:'margin-left:5px;',
				   text:'保存',
				   scope:this,
				   handler:function(btn){
					   var grid=Ext.getCmp('togrid'),fromgrid=Ext.getCmp('fromgrid');
					   var jsonGridData = new Array(),datas=new Array();
						var form = Ext.getCmp('form');
						grid.getStore().each(function(item){//将grid里面各行的数据获取并拼成jsonGridData
							var data = {
							  dde_field:item.data.fullName,
							  dde_width:item.data.width,
							  dde_orderby:item.data.orderby && item.data.orderby!='ASC' && item.data.orderby!='DESC' ?null:item.data.orderby,
							  dde_priority:item.data.priority
							}; 
							item.dirty=false;
                        	jsonGridData.push(Ext.JSON.encode(data));
                        	datas.push(item.data);
						});
					   Ext.Ajax.request({
							url : basePath + 'common/saveEmpsDataListDetails.action?_noc=1',
							params : {
								caller:caller,
								data:unescape(jsonGridData.toString())
							},
							method : 'post',
							callback : function(options,success,response){
								var localJson = new Ext.decode(response.responseText);
								if(localJson.success){
									showMessage('提示','保存成功!',1000);
									window.location.reload();
								}
							}

						});
				   }
			   },{
				   style:'margin-left:5px;',
				   text:'关闭',
				   handler:function(btn){
					   btn.ownerCt.ownerCt.hide();
				   }
			   },'->','->']
			});
		}
		win.show();
	}
});