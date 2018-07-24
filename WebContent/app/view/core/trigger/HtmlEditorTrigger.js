/**
 * HtmlEditorTrigger
 */
Ext.define('erp.view.core.trigger.HtmlEditorTrigger', {
    extend: 'Ext.form.field.Trigger',
    alias: 'widget.HtmlEditorTrigger',
    triggerCls: 'x-form-textarea-trigger',
    onTriggerClick: function() {
    	
        var trigger = this,
        value = this.value;
    	var filterPanel = Ext.create('Ext.window.Window', {
   				bodyPadding: 0,  // 避免Panel中的子元素紧邻边框
    			id:'fwindows',
    			width: 600,
    			height: 300,
    			xtype:'form',
    			autoScroll:true,
    			layout: 'anchor',
    			title: '详细内容',
    			items: [{
       				 	xtype: 'htmleditor',
       				 	id:'htmleditor',      				 	
   				 	  	autoScroll:false,
   				 	  	anchor: '100% 100%',
        				listeners: {
        						beforerender:function(){        							
        							var name=trigger.record.get(trigger.name);
        							Ext.getCmp('htmleditor').setValue(name)
        						}
        					}
   			 		}],
   			 	buttons:[{
   			 		 text: '确定',
       				 handler: function(btn) {       				       				 	
       				 	var text =Ext.getCmp('htmleditor').getValue();  
       				 	console.log(text);
       				 	//trigger.editable &&
       				 	  if ( btn.text == '确定') {       				 	  	
                				if (trigger.ownerCt === undefined) {
                    				trigger.record.set(trigger.name, text);
               			 		} else {
                   			 		trigger.setValue(text);
               						 } 
               					}
           		Ext.getCmp('fwindows').close();
           				 
        			}
   			 		
   			 	}],
    			renderTo: Ext.getBody()
				});
    		filterPanel.show();   	
    },
    listeners: {
        focus: function(trigger) {
            if (trigger.ownerCt === undefined) {
                try {
                    var grid = Ext.getCmp(trigger.el.dom.parentNode.offsetParent.offsetParent.id);
                    if (grid !== undefined) {
                        trigger.owner = grid;
                        if(grid.readOnly){
                        	Ext.Array.forEach(grid.columns,function(c){
								if(c.dataIndex==trigger.owner.editingPlugin.activeEditor.field.name){
									if(!c.modify){
										trigger.setFieldStyle('background:#e0e0e0;');
										trigger.setEditable(false);
									}
								}
							});
                        }
                        if (trigger.owner.editingPlugin.activeEditor.field.id == trigger.id) {
                            trigger.record = trigger.owner.editingPlugin.activeRecord;
                        } else {
                            trigger.record = trigger.owner.selModel.lastSelected;
                        }
                    }
                } catch(e) {

                }
            }
        }
    },
    initComponent: function() {
    	if(!contains(this.id, '-', true)){
    		this.editable = !this.readOnly;
    	}else{
    		if(!this.editable){
    			this.fieldStyle= 'background:#e0e0e0;';
    		}
    	}
    	this.readOnly = false;//always show trigger
        this.callParent(arguments);
    },
    setReadOnly :function(bool){//流程审批时设置对应分组字段是可编辑状态
    	this.readOnly=false;
    	this.setEditable(!bool);
    }
});