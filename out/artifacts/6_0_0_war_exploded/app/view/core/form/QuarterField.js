/**
 * 选择季度picker
 */
Ext.define('erp.view.core.form.QuarterField', {
	extend : 'Ext.form.FieldContainer',
	alias : 'widget.quarterfield',
	layout: 'column',
	items: [],
	 initComponent : function(){
		 this.callParent(arguments);
		 var me=this;
		 var quarter = Ext.create('Ext.data.Store', {
			    fields: ['key', 'value'],
			    data : [
			        {"key":"第一季度", "value":"Q1"},
			        {"key":"第二季度", "value":"Q2"},
			        {"key":"第三季度", "value":"Q3"},
			        {"key":"第四季度","value":"Q4"}
			    ]
			});
		 me.insert(0,Ext.create('erp.view.core.form.YearDateField',{
			 xtype:'yeardatefield',
			 id:'kd_time2_a',
			 columnWidth: 0.5,
			 editable:false,
			 listeners : {
					afterrender : function(field) {
						var m=Number(Ext.Date.format(new Date(), 'm'));
						var y=Number(Ext.Date.format(new Date(), 'Y'));
						if(m>0&&m<=3){
							field.setValue(y-1);
							field.setMaxValue(y-1);
						}else{
							field.setMaxValue(y);
						}			
					}
			 }
		 }));
		 me.insert(1,Ext.create('Ext.form.ComboBox',{
			 xtype:'combo',
			 id:'kd_time2_b',
			 editable:false,
			 columnWidth: 0.5,
 			 store: quarter,
 		     queryMode: 'local',
 		     displayField: 'key',
 		     valueField: 'value',
 		     listeners : {
				afterrender : function(c) {
					var m=Number(Ext.Date.format(new Date(), 'm'));
					if(m>0&&m<=3){
						c.setValue('Q4');
					}else if(m>3&&m<=6){
						c.setValue('Q1');
					}else if(m>6&&m<=9){
						c.setValue('Q2');
					}else if(m>9&&m<=12){
						c.setValue('Q3');
					}
				}
			}
		 }));	    	
	 }
});