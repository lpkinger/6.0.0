Ext.define('erp.view.core.form.HrefField', {
	extend: 'Ext.form.FieldContainer',
    alias: 'widget.hreffield',
    autoScroll:true,
    title: '',
    height: 22,
    initComponent: function() {
    	this.callParent(arguments);
    	var me = this;
    	/*me.insert(0, {
	        xtype: 'hidden',  
	        editable: false,
	        name:me.name,
	        columnWidth:0,
	        value:Ext.isNumber(me.value)?me.value:0,		
	    });
    	var fieldvalue=me.items.items[0].value;
    	var picvalue="";
    	if(fieldvalue){    		
    		for(var i=0;i<Number(fieldvalue);i++){
    			picvalue+='<img src="'+basePath+'resource/images/renderer/remind.png">';
    		}
    	}*/
    	me.insert(0, {xtype: 'htmleditor',
	        enableColors: false,
	        enableAlignments: false,
	        columnWidth:1,
	        enableFont: false,
	        enableFontSize: false,
	        enableFormat: false,
	        enableLinks: false,
	        enableLists: false,
	        enableSourceEdit: false,
			name: me.name,
			cls :'form-field-allowBlank',
			style:'background:#fffac0;color:#515151;',
			frame: false,
			value:me.value,
			logic:me.logic,
			height:22,
		    listeners: {
		    	afterrender: function(editor){
		    		editor.getToolbar().hide();		    		
		    		/*this.getEl().dom.addEventListener('click',function(){
		    			if(editor.getValue()){
		    			editor.setValue(editor.getValue()+'<img src="'+basePath+'resource/images/renderer/remind.png">');
		    			}else editor.setValue('<img src="'+basePath+'resource/images/renderer/remind.png">');
		    			
		    			editor.ownerCt.items.items[0].setValue(Number(editor.ownerCt.items.items[0].value)+1);
		    		});*/
		    	},
		    	
		    },
			});
    },
    layout:'column',
    setValue: function(value){
    	this.value = value;
    },
    items:[],
    listeners: {
    	afterrender: function(field){
    		if(field.value){
    			field.setValue(field.value);
    		}
    		this.getEl().dom.childNodes[1].style.height = 22;
    		this.getEl().dom.childNodes[1].style.overflow = 'hidden';    		
    	}
    },
	addItem: function(item){
		this.add(item);
	}
});