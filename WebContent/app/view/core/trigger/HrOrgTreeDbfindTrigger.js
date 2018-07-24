/**
 *  multi dbfind trigger
 * 
 */
Ext.define('erp.view.core.trigger.HrOrgTreeDbfindTrigger', {
    				   extend: 'Ext.form.field.Trigger',
    				   alias: 'widget.hrOrgTreeDbfindTrigger',
    				   triggerCls: 'x-form-search-trigger',
     				    onTriggerClick: function() {
    				   	var trigger = this;//放大镜所在
    				   	var key = this.name;//name属性
    				   	var value = this.value;
    				   	var dbfind = '';//需要dbfind的表和字段
    				   	var keyValue = this.value;//当前值
	   			    	var dbwin = new Ext.window.Window({
	   			    		id : 'dbwin',
		   				    title: '查找',
		   				    height: "100%",
		   				    width: "80%",
		   				    maximizable : true,
		   					buttonAlign : 'center',
		   					layout : 'anchor',
		   				    items: [],
		   				    buttons : [{
		   				    	text : '确  认',
		   				    	iconCls: 'x-button-icon-save',
		   				    	cls: 'x-btn-gray',
		   				    	handler : function(){
		   				    		var contentwindow = Ext.getCmp('dbwin').body.dom.getElementsByTagName('iframe')[0].contentWindow;
		   				    		var tree = contentwindow.Ext.getCmp('tree-panel');
		   				    		var data = tree.getChecked();
		   				    		var value = null;
		   				    		for(i=0;i<data.length;i++){
		   				    			if(i==0){
		   				    				value = data[i].data.text;
		   				    				if(value.indexOf('(')){
		   				    					value = value.split('(');
		   				    					value = value[0];
		   				    				}
		   				    			}else{
		   				    				if(value.indexOf('(')){
		   				    					value = value.split('(');
		   				    					value = value[0];
		   				    				}
		   				    				value = value+"#"+data[i].data.text;
		   				    			}
		   				    		}
		   				    		trigger.setValue(value);
		   				    		Ext.getCmp('dbwin').close();
		   				    	}
		   				    },{
		   				    	text : '关  闭',
		   				    	iconCls: 'x-button-icon-close',
		   				    	cls: 'x-btn-gray',
		   				    	handler : function(){
		   				    		Ext.getCmp('dbwin').close();
		   				    	}
		   				    }]
		   				});
   			    		dbwin.add({
   			    		    tag : 'iframe',
   			    		    frame : true,
   			    		    anchor : '100% 100%',
   			    		    layout : 'fit',
   			    		    html : '<iframe id="iframe_dbfind_'+caller+"_"+key+"="+keyValue+'" src="'+basePath+'jsps/common/treepaneldbfind.jsp?key='+key+"&dbfind="+dbfind+"&keyValue=&trigger="+trigger.id+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
   			    		});
		   				dbwin.show();
    				   }
    				});