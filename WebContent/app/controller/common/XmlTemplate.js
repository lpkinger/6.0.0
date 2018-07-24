Ext.QuickTips.init();
Ext.define('erp.controller.common.XmlTemplate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'common.XmlTemplate','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
			'core.form.YnField','core.form.TimeMinuteField','core.trigger.DbfindTrigger','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
        init:function(){
        	var me = this;
        	this.control({ 
        		'textarea':{
        			afterrender:function(cmp){
        				cmp.getEl().set({
			                'spellcheck': 'false'
			            });
        			}
        		},
        		'erpAddButton':{
        			click:function(btn){
        				me.FormUtil.onAdd('xmltemplate','新增','jsps/common/XmlTemplate.jsp');
        			}
        		},
        		'erpFormPanel':{
        			afterload:function(form){
        				var content = Ext.getCmp('xt_content');
        				if(content){
        					var width = form.getWidth();
        					var height = form.getHeight();
        					content.setHeight(height);
        					content.setWidth(width-15);
        				}
        			}
        		},
        		'erpSaveButton': {
        			click: function(btn){
        				var bool = me.validateXmlFormat();
        				if(bool){
        					this.FormUtil.beforeSave(this);
        				}					
        			}
        		},
        		'erpUpdateButton':{
        			click:function(btn){      				
        				var bool = me.validateXmlFormat();
        				if(bool){
        					this.FormUtil.onUpdate(this);
        				}
        			}
        		},
        		'erpCloseButton': {
        			click: function(btn){
        				this.FormUtil.beforeClose(this);				
        			}
        		},
        		'erpDeleteButton':{
        			click:function(btn){
        				this.FormUtil.onDelete(Ext.getCmp('xt_id').value);
        			}
        		}
        	});
        },
        getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	validateXmlFormat:function(){
			if(DOMParser){
		        var parser = new DOMParser(),
		            xmldom, 
		            errors;
		        var xmldata = Ext.getCmp('xt_content').value;  
		        try {
		            xmldom = parser.parseFromString(xmldata, "text/xml");
		            errors = xmldom.getElementsByTagName("parsererror");
		            if (errors.length > 0){
		                throw new Error("XML Parsing Error:" + (new XMLSerializer()).serializeToString(xmldom, "text/xml"));
		            }
		            return true;
		        } catch (ex) {
		            //alert(ex.message);
		        	showError('xml格式有误,请重新修改!');
		        	return false;
		        }        					
			}else{
				return true;
			}
    	}
});