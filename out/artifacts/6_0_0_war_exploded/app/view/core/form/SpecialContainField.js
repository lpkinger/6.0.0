/**
 * 
 */
Ext.define('erp.view.core.form.SpecialContainField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.specialcontianfield',
    height: 27,
    items: [],
    layout:'column',
    columnWidth:1,
    showscope: true,
    initComponent : function(){
    	this.callParent(arguments);
    	var me = this;
		var logic=this.logic;
		var value=this.value;		
		var arr=logic.split(";");
		var valuearr=value.split(";");
		Ext.Array.each(arr,function(item,index){
			if(logic.indexOf(";")>0){
				me.insert(0,{
					xtype:'hidden',
					name:me.name,
					id:me.name+"_checks",
					value:me.value
					//margin:'50 0 0 0'
				});		
				if(item.indexOf('checkbox')==0 && item.indexOf("#")>0){
					me.insert(index+1,{
						xtype:'checkbox',
						columnWidth:0.4,
						boxLabel:item.split("#")[1],
						labelSeparator:'',
						boxLabelAlign:'before',
						labelWidth:0,
						margin:'0 0 0 120',
						checked:valuearr[index]==1,
						listeners:{
							change:function(field,newValue,oldValue){
								if(newValue){
									me.setFiledValue();
								}else {
									me.setFiledValue();
								}
							}
						}
					});
				}else if(item=='combo'){
					me.insert(index+1,{
						xtype:'combo',
						hideLabel:true
					});
				}else if(item=='text'){
					me.insert(index+1,{
						xtype:'textfield',
						hideLabel:true,
						columnWidth:0.6,
						labelWidth:0,
						value:valuearr[index],
						margin:'0 0 0 -55',
						fieldStyle:'background:#FFFAFA;color:#515151;',
						listeners:{
							change:function(field,newValue,oldValue){
								if(newValue){
									me.setFiledValue();
								}else {
									me.setFiledValue();
								}
							}
						}
					});
				}else{
					me.insert(index+1,{
						 xtype: 'displayfield',
						 fieldStyle:'color:black;font-size:15px;margin-left:150px',
	                     value: item	                               
					});				
				}
			}else {
				me.insert(0,{
					xtype:'textfield',
					labelWidth:400,
					columnWidth:0.9,
					name:me.name,
					value:me.value,
					id:me.name+'_checks',
					fieldStyle:'background:#FFFAFA;color:#515151;',
					fieldLabel:item.split('#')[1]
				});
			}
			
		});
    },
    setFiledValue:function(){
    	var value="";
		var items=this.items.items;
		for(var i=1;i<items.length;i++){
			if(items[i].xtype=='checkbox'){
				if(items[i].value){
					value+="1;";
				}else {
					value+="-1;";
				}
			}else {
				if(items[i].value){
					value +=items[i].value+";";
				}else value +=";";
				
			}			
		}
		value=value.substring(0,value.length-1);
		this.items.items[0].setValue(value);
    },
    isValid: function(){
		return this.items.items[0].isValid();
	},
	setValue: function(value){
		this.value=value;
	},
	isDirty:function(){
		return true;
	},
	listeners: {
    	afterrender: function(){
    		if(this.getEl().dom.childNodes>1){
    			this.getEl().dom.childNodes[1].style.height = 22;
				this.getEl().dom.childNodes[1].style.overflow = 'hidden';
    		}
    	
    	}
    }
});