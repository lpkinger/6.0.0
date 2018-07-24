/*
 * Ext JS Library 3.2.0
 * Copyright(c) 2006-2010 Ext JS, Inc.
 * licensing@extjs.com
 * http://www.extjs.com/license
 * we did some changes based on extjs's code
 */
Ext.ns('Ext.ux.form');

Ext.ux.form.SearchField = Ext.extend(Ext.form.TwinTriggerField, {
    initComponent : function(){
        Ext.ux.form.SearchField.superclass.initComponent.call(this);
        this.on('specialkey', function(f, e){
            if(e.getKey() == e.ENTER){
                this.onTrigger2Click();
            }
        }, this);
        this.addEvents(
        	'beforesearch',
        	'search'
        );
    },
    /*
     * the fields to search, if undefined or null, then use all fields
     */
    //searchFields:{'name':true},
    mode:'remote',
    validationEvent:false,
    validateOnBlur:false,
    trigger1Class:'x-form-clear-trigger',
    trigger2Class:'x-form-search-trigger',
    hideTrigger1:true,
    width:180,
    hasSearch : false,
    paramName : 'query',

    onTrigger1Click : function(){
        if(this.hasSearch){
            this.el.dom.value = '';
            if(false !== this.fireEvent('beforesearch', null, this.store, this)){
	            if('local' != this.mode){
		            var o = {start: 0};
                            if (this.limit) {
                                o = {start: 0, limit: this.limit};
                            }
		            this.store.baseParams = this.store.baseParams || {};
		            this.store.baseParams[this.paramName] = '';
		            this.store.reload({params:o});
	            }else{
	            	this.store.clearFilter();
	            }
	            this.fireEvent('search', null, this.store, this);
            }
            this.triggers[0].hide();
            this.hasSearch = false;
        }
    },

    onTrigger2Click : function(){
        var v = this.getRawValue();
        if(v.length < 1){
            this.onTrigger1Click();
            return;
        }
        if(false !== this.fireEvent('beforesearch', v, this.store, this)){
	        if('local' != this.mode){
		        var o = {start: 0};
                        if (this.limit) {
                            o = {start: 0, limit: this.limit};
                        }
		        this.store.baseParams = this.store.baseParams || {};
		        this.store.baseParams[this.paramName] = v;
		        this.store.reload({params:o});
	        }else{
	        	var er = Ext.escapeRe;	        		            	           
	        	var reg = new RegExp('^' + er(String(v)), 'i');	        	
	        	this.store.filterBy(function(rd, id){
	        		var fields = rd.fields;
	        		var match = false;
	        		fields.each(function(it){
	        			if(!this.searchFields || this.searchFields[it.name]){
		        			if(reg.test(rd.data[it.name])){
		        				match = true;
		        				return false;
		        			}	        				
	        			}
	        		}, this);
	        		return match;
	        	}, this);
	        }
	        this.fireEvent('search', v, this.store, this);
        }
        this.hasSearch = true;
        this.triggers[0].show();
    }
});