/**
 * 级联国家省份城市下拉框
 * @author chenp
 */
Ext.define('erp.view.core.form.CascadingCityField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.cascadingcityfield',
    layout: 'hbox',
    items: [], 
    showscope: true,
    initComponent : function(){
      	this.cls = (this.cls || '') + ' x-form-field-multi';
    	this.callParent(arguments);
    	var me = this;
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
    	var combo0=Ext.create('Ext.form.field.ComboBox', {
    		flex:0.33,    
    		editable: true,
    		groupName:me.groupName,
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
			value:me.value,
    	    listeners: {
    	    	focus:function(){
    	    		this.store.loadData(counData);
    	    	},
                select: function (combo, record, index) {
                	me.items.items[3].setValue(record[0].data.display);              	
            		var provCombo = Ext.getCmp(me.secondname.split('#')[0]+'Combo');
            		provCombo.clearValue();
            		if (combo.store.isFiltered())combo.store.clearFilter(true);
            		/*if(this.store.snapshot && this.store.snapshot != this.store.data){ 
            		this.store.loadData(this.store.snapshot);}*/
            		proIndex='0_'+combo.store.find('display',record[0].data.display); 
            		var index = myStore.find('id',proIndex);   
            		tmpData = myStore.getAt(index).get('name');
            		provData=[];
            		for (var i = 0; i < tmpData.length; i++) {
            			provData.push([tmpData[i]]);
            		}											
            		provCombo.setValue(provData[0]);
            		Ext.getCmp(me.secondname.split('#')[0]).setValue(provData[0]);
            		provCombo.store.loadData(provData);	
            		
            		var cityCombo = Ext.getCmp(me.secondname.split('#')[1]+'Combo');												
            		cityCombo.clearValue();											
            		tmpData = myStore.getAt(myStore.find('id',proIndex+'_0')).get('name');
            		cityData=[];
            		for (var i = 0; i < tmpData.length; i++) {
            			cityData.push([tmpData[i]]);
            		}											
            		cityCombo.setValue(cityData[0]);
            		Ext.getCmp(me.secondname.split('#')[1]).setValue(cityData[0]);
            		cityCombo.store.loadData(cityData);	
                
                }
    	    }
    	});
    	var combo1=Ext.create('Ext.form.field.ComboBox', {
    		id:me.secondname.split('#')[0]+'Combo',
    		flex:0.33,
    		editable: true,
    		groupName:me.groupName,
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
    	    value:Ext.getCmp(me.secondname.split('#')[0])?Ext.getCmp(me.secondname.split('#')[0]).getValue():'',
	        listeners: {
	        	select: function (combo, record, index) {	
	        		Ext.getCmp(me.secondname.split('#')[0]).setValue(record[0].data.display);
	        		var cityCombo = Ext.getCmp(me.secondname.split('#')[1]+'Combo');
	        		cityCombo.clearValue();	
	        		if (combo.store.isFiltered())combo.store.clearFilter(true); 	      
	        		var index = myStore.find('id',proIndex+'_'+combo.store.find('display',record[0].data.display));
	        		tmpData = myStore.getAt(index).get('name');
	        		cityData=[];
	        		for (var i = 0; i < tmpData.length; i++) {
	        			cityData.push([tmpData[i]]);
	        		}	
	        		cityCombo.setValue(cityData[0]);
	        		Ext.getCmp(me.secondname.split('#')[1]).setValue(cityData[0]);
	        		cityCombo.store.loadData(cityData);	
	            
	        	}
	        }
	    });
    	var combo2=Ext.create('Ext.form.field.ComboBox', {
    		id:me.secondname.split('#')[1]+'Combo',
    		flex:0.33,
    		editable: true,
    		groupName:me.groupName,
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
    	    value:Ext.getCmp(me.secondname.split('#')[1])?Ext.getCmp(me.secondname.split('#')[1]).getValue():'',
    	    listeners: {
	        	select: function (combo, record, index) {
	        		Ext.getCmp(me.secondname.split('#')[1]).setValue(record[0].data.display);
	        	}
    	}
	    });	
    	myStore.load({
    		scope : myStore,
    		callback : function(records, options, success){ 
    			var index = myStore.find('id','0');
    			tmpData = myStore.getAt(index).get('name');
    			for (var i = 0; i < tmpData.length; i++) {
    				counData.push([tmpData[i]]);
    			}
    	    	me.insert(0,combo0);    	    	
    	    	me.insert(1,combo1);
    	    	me.insert(2,combo2);
    			me.insert(3,{
    				xtype:'hidden',
    				name:me.name,
    				value:me.value
    			});
    		}
    	});
    }
});