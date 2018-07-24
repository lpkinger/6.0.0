/**
 * 修改明细按钮
 */	
Ext.define('erp.view.core.button.EditDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpEditDetailButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpEditDetailButton,
    	id:'editDetail',
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler:function(self,e,grids,neccessaryField){
			if(!neccessaryField){
				Ext.Msg.alert('提示','流程按钮没有设置必填字段');
				return;
			}
			
			Ext.Array.each(grids,function(grid,index){
				grid.readOnly=false;
				grid.setDisabled(false);
				var fields = neccessaryField.split(","),addItems=new Array(),fieldtype=null,editable=false;														
				Ext.Array.each(grid.columns,function(column){
					editable=false;
					Ext.Array.each(fields,function(field) {
						var f =column.xtype;													
						if(column.dataIndex==field){
							column.neccessaryField=true;
							editable=true;
							if (f=="numbercolumn") {																
								column.editor={
										xtype:'numberfield',
										format:'0',
										hideTrigger:true
								};
							} else if (f=="floatcolumn") {
								column.editor={
										xtype:'numberfield',
										format:'0.00',
										hideTrigger:true
								};
							} else if (f.indexOf("floatcolumn")>-1) {							
								var format = "0.";
								var length =parseInt(f.substring(11));
								for (var i = 0; i < length; i++) {
									format += "0";
								}
								column.editor={
										xtype:'numberfield',
										format:format,
										hideTrigger:true
								};
							} else if (f =="datecolumn") {
								column.editor={
										xtype:'datefield',
										format:"Y-m-d",
										hideTrigger:false
								};
							} else if (f =="datetimecolumn") {
								column.editor={
										xtype:'datetimefield',
										format:"Y-m-d H:i:s",
										hideTrigger:false
								};
							} else if (f =="timecolumn") {
								column.editor={
										xtype:'timefield',
										format:"H:i",
										hideTrigger:false
								};
							} else if (f =="monthcolumn") {
								column.editor={
										xtype:'monthdatefield',
										hideTrigger:false
								};
							} else if (f =="textcolumn" || f=="textfield" || f=="text") {	
								column.editor={
										xtype:'textfield'
								};
							} else if (f =="textareafield") {			
								column.editor={
										xtype:'textareafield'
								};
							} else if (f=="textareatrigger") {
								column.editor={
									xtype:'textareatrigger',
									hideTrigger:false
								};
							} else if (f=="dbfindtrigger") {					
								column.editor={
										xtype:'dbfindtrigger',
										hideTrigger:false
									};
							} else if (f =="multidbfindtrigger") {
								column.editor={
										xtype:'multidbfindtrigger',
										hideTrigger:false
									};
							} else if (f=="datehourminutefield") {					
								column.editor={
										xtype:'datehourminutefield',
										hideTrigger:false
									};
							} else if (f=="checkbox") {
								column.editor={
										xtype:'checkbox',
										cls:'x-grid-checkheader-editor',
										hideTrigger:false
									};
							}
							return false;
						}
					});
					if(!editable) {
						column.editor=null;
					}
				});
			});
			Ext.Msg.alert('提示','明细行已设为可编辑');
		}
	});