/**
 * 根据地方选择银行名称
 * @author guq
 */
Ext.define('erp.view.core.trigger.BankNameTrigger',{
	extend:"Ext.form.field.Trigger",
	alias:'widget.bankNameTrigger',
	triggerCls: 'x-form-search-trigger',
	editable: false,
	condition:'%',
	initComponent: function() {
		var me = this;
		me.addEvents({
			aftertrigger: true,
			beforetrigger: true
		});
		if(!me.ownerCt) {
			Ext.defer(function(){
				me.getOwner();
			}, 50);
		}
		this.displayField='display';	   
		this.valueField='value';
		this.queryMode='local';
		if(me.clearable) {
			me.trigger2Cls = 'x-form-clear-trigger';
			if(!me.onTrigger2Click) {
				me.onTrigger2Click = function(){
					this.setValue(null);
				};
			}
		}
		if(me.value){
			me.store=Ext.create('Ext.data.Store', {
				   fields: ['display','value'],
				   data : [{
					   'display':me.value,
					   'value':me.value
				   }]
			});
		}
		me.displayTpl='<tpl for=".">' +
        '{[typeof values === "object" ?  values["' + me.displayField + '"]:values]}' +
        '<tpl if="xindex < xcount">' + me.delimiter + '</tpl>' +
       '</tpl>';
		me.callParent(arguments);
	},
	getOwner: function() {
		var me = this;
		if (me.el) {
			var gridEl = me.el.up('.x-grid');
			if (gridEl) {
				var grid = Ext.getCmp(gridEl.id);
				if (grid) {
					me.owner = grid;
					me.column = grid.down('gridcolumn[dataIndex=' + me.name + ']');
				}
			}
		}
	},
	onTriggerClick: function() {
		var me=this;
		var width = Ext.isIE ? screen.width * 0.7 * 0.9 : '80%',
				height = Ext.isIE ? screen.height * 0.75 : '95%';
		//针对有些特殊窗口显示较小
		width =this.winWidth ? this.winWidth:width;
		height=this.winHeight ? this.winHeight:height;
		var tmpData = [];
    	var counData = [];
    	var provData = [];
    	var cityData = [];
    	var proIndex = '';
		var myStore = new Ext.data.Store({
    		proxy : {
    			type : 'ajax',
    			url : basePath + 'app/store/ma/CityComboStore/city.json'
    		},
    		autoLoad : false,
    		fields : [{
    					name : 'id'
    				}, {
    					name : 'name'
    				}]
    	});
		var proData=[];
		var cityData=[];
		var counData=[];
		var store=me.createStore();
		var dbwin = new Ext.window.Window({
			id: 'dbwin',
			title: '银行行名行号查找',
			height: height,
			width: width,
			maximizable: true,
			buttonAlign: 'center',
			layout: 'anchor',
			items: [{
				xtype:'form',
				anchor:"100% 5%",
				layout:"hbox",
				items:[{
					xtype:"combo",
					flex:0.35,
					id:"country",
					fieldLabel:"选择地区",
					labelAlign:"left",
					labelWidth:70,
					labelStyle:"font-weight:bold",
					margin:'0 4 0 0',
					editable: true,
					growToLongestValue :true,
		    		store: Ext.create('Ext.data.Store', {
		    			fields : [ {name : 'display'} ],
		    			data : counData,
		    			autoLoad : true
		    			}),
		    		queryMode: 'local', 
		    	    displayField: 'display',
		    	    valueField: 'display', 
		    	    mode : 'local',
					triggerAction : 'all',  
					value:Ext.getCmp('country')?Ext.getCmp('country').getValue():'',
		    	    listeners: {
		    	    	focus:function(){
		    	    		this.store.loadData(counData);
		    	    	},
		                select: function (combo, record, index) {          	
		            		var provCombo = Ext.getCmp('province');
		            		provCombo.clearValue();
		            		if (combo.store.isFiltered())combo.store.clearFilter(true);
		            		proIndex='0_0_'+combo.store.find('display',record[0].data.display); 
		            		var index = myStore.find('id',proIndex);   
		            		tmpData = myStore.getAt(index).get('name');
		            		provData=[];
		            		for (var i = 0; i < tmpData.length; i++) {
		            			provData.push([tmpData[i]]);
		            		}
		            		provCombo.setValue(provData[0]);
		            		provCombo.store.loadData(provData);	
		            		
		            		var cityCombo = Ext.getCmp('city');												
		            		cityCombo.clearValue();
		            		if (myStore.getAt(myStore.find('id',proIndex+'_0'))){
		            			tmpData = myStore.getAt(myStore.find('id',proIndex+'_0')).get('name');
		            		} else {
		            			tmpData=[];
		            		}		            		
		            		cityData=[];
		            		for (var i = 0; i < tmpData.length; i++) {
		            			cityData.push([tmpData[i]]);
		            		}		
		            		cityCombo.setValue(cityData[0]);
		            		cityCombo.store.loadData(cityData);
		            		me.search();
		                }
		    	    }
				},{
					xtype:"combo",
					flex:0.3,
					id:'province',
					margin:'0 4 0 0',
					editable: true,
					growToLongestValue :true,
		    		store: Ext.create('Ext.data.Store', {
		    			fields : [ {name : 'display'} ],
		    			data : provData,
		    			autoLoad : true
		    			}),
		    	    queryMode: 'local', 
		    	    mode : 'local',
					triggerAction : 'all',
		    	    displayField: 'display',
		    	    valueField: 'display',
		    	    value:Ext.getCmp('province')?Ext.getCmp('province').getValue():'',
			        listeners: {
			        	select: function (combo, record, index) {	
			        		Ext.getCmp('province').setValue(record[0].data.display);
			        		var cityCombo = Ext.getCmp('city');
			        		cityCombo.clearValue();	
			        		if (combo.store.isFiltered())combo.store.clearFilter(true); 	      
			        		var index = myStore.find('id',proIndex+'_'+combo.store.find('display',record[0].data.display));
			        		tmpData = myStore.getAt(index)?myStore.getAt(index).get('name'):[];
			        		cityData=[];
			        		for (var i = 0; i < tmpData.length; i++) {
			        			cityData.push([tmpData[i]]);
			        		}	
			        		cityCombo.setValue(cityData[0]);
			        		cityCombo.store.loadData(cityData);	
			        		me.search();
			        	}
			        }
				},{
					xtype:"combo",
					id:'city',
					flex:0.3,
					margin:'0 10 0 0',
					editable: true,
					growToLongestValue :true,
		    		store: Ext.create('Ext.data.Store', {
		    			fields : [ {name : 'display'} ],
		    			data : cityData,
		    			autoLoad : true
		    			}),
		    	    queryMode: 'local', 
		    	    mode : 'local',
					triggerAction : 'all',
		    	    displayField: 'display',
		    	    valueField: 'display',
		    	    value:Ext.getCmp('city')?Ext.getCmp('city').getValue():'',
		    	    listeners: {
			        	select: function (combo, record, index) {
			        		Ext.getCmp('city').setValue(record[0].data.display);
			        		me.search();
			        	}
		    	}
				},{
					xtype:'textfield',
					flex:0.7,
					margin:'0 10 0 0',
					id:'bank',
					fieldLabel:"银行信息",
					labelAlign:"left",
					labelWidth:70,
					labelStyle:"font-weight:bold",
					emptyText:"请填写完整行名,如:浦发->浦东发展",
					enableKeyEvents:true,
					listeners:{
					    blur:function(field,e,opts){
					    	me.search();
					    },
					    keydown:function(field,e,opts){
					    	if(e.keyCode=13){
					    		me.search();
					    	}
					    }
					}
				}],
				},{
				xtype:"grid",
				id:'bankgrid',
				anchor:"100% 95%",
			//plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
				dockedItems: [{
					xtype:"pagingtoolbar",
			        dock: 'bottom',
			        store:store,
			        emptyMsg:"数据为空",
			        displayMsg:"显示第{0}-{1}行数,总共{2}条记录",
			        displayInfo: true
				}],
				selModel:Ext.create('Ext.selection.CheckboxModel',{
					headerWidth: 0,
				}),
				columns:[],
				store:store,
				listeners:{
					itemclick:function(view,record,item,index,e,opts){		
						var value=record.data.bankname,code=record.data.bankcode,codeField;
						if(me.owner){//点击从表
							var grid = me.owner;
							var select = grid.selModel.lastSelected;
							select.set(me.column.dataIndex,value);
							if (caller == 'VendorBank') {
								select.set('vpd_bankcode',code);
							} else if ( caller == 'PayPlease!YF' || caller =='PayPlease') {
								select.set('ppd_bankcode',code);
							}
						}else{//点击主表								
								if(value){
									me.setValue(value);
									}
							if (caller =='FeePlease!FYBX' || caller=='FeePlease!JKSQ') {
								codeField = Ext.getCmp('fp_bankcode');
							} else if ( caller =='Vendor') {
								codeField = Ext.getCmp('ve_bankcode');
							}
							if(codeField) {
								codeField.setValue(code);
							}
						}	
						dbwin.close();
					}
				}
			}],
			buttons: [{
				text: '关  闭',
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				handler: function() {
					Ext.getCmp('dbwin').close();
				}
			},
			{
				text: '重置条件',
				id: 'reset',
				cls: 'x-btn-gray',
				handler: function() {
					var grid=Ext.getCmp('bankgrid'),prov=Ext.getCmp('country'),city=Ext.getCmp('province'),county=Ext.getCmp('city'),kind=Ext.getCmp('bank');
					prov.setValue('');
					city.setValue('');
					county.setValue('');
					kind.setValue('');
					me.condition=null;
					grid.store.load();
				}
			}]
		});
		dbwin.show();
		var bank=Ext.getCmp('bankgrid');
		bank.reconfigure(store,me.createColumn());
		myStore.load({
    		scope : myStore,
    		callback : function(records, options, success){ 
    			var index = myStore.find('id','0_0');
    			tmpData = myStore.getAt(index).get('name');
    			for (var i = 0; i < tmpData.length; i++) {
    				counData.push([tmpData[i]]);
    			}
    		}
    	});
	},
	createColumn:function(){
		var arr=[{		
				xtype: 'rownumberer',
				width: 35,
				align:'cener',
		},{
			header:'银行行名',
			dataIndex:"bankname",
			width:350,
		},{
			header:'银行行号',
			dataIndex:"bankcode",
			width:150,
		},{
			header:'银行汇路',
			dataIndex:"bankway",
			width:100,
			renderer:function(val){
				if(val=='3'){
					return '非网银汇路';
				}
				else if(val=='5')
					return '网银汇路';
			}
		
		}];
		Ext.each(arr,function(obj){
			if(obj.xtype!='rownumberer'){
				obj.filter= {
	 			         dataIndex:obj.dataIndex,
	 			         xtype: "textfield",
	 			      };
				obj.filterJson_={};
			}
		});
		return arr;	
	},
	createStore:function(){
		var me=this;
		return Ext.create("Ext.data.Store",{
			fields:["bankname","bankcode","bankway"],
			pageSize:15,
			proxy:{
				type:'ajax',
				url:basePath+'common/getBankName.action',
				actionMethods: {
		            read   : 'POST'
		        },
				reader:{
					type:'json',
					root:'data',
					totalProperty:'num',
					}
			},
			listeners:{
				beforeload : function(store) {
						Ext.apply(store.proxy.extraParams, {
							condition:me.condition,
						});
				},
			},
			autoLoad:true,
		});
	},
	search:function(){
		var city=Ext.getCmp('province').value,county=Ext.getCmp('city').value,kind=Ext.getCmp('bank').value,me=this;
		if(county){
			if(county.indexOf('区')>-1||county.indexOf('县')>-1){
    			county=county.substring(0,county.length-1);
    		}
		}           		
		me.condition='%'+(kind?kind:'')+'%'+(city?city:'')+'%'+(county?county:'')+'%';
		Ext.getCmp('bankgrid').store.load();
	}
});