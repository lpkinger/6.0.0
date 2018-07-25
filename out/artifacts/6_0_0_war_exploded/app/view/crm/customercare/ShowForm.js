Ext.define('erp.view.crm.customercare.ShowForm',{
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpShowForm',
		layout : 'column',
		winurl:'',
		id:'showform',
		title:'',
		caller:'',
		formCondition:'',
		gridCondition:'',
		autoScroll : true,
	    buttons: [{ 
	    			text: '编辑',
	    			handler:function(){
	    				var form=this.up('form');
	    				var s=basePath+form.winurl+'?formCondition='+form.formCondition+'&girdCondition='+form.girdCondition;//Ext.getCmp('mt_id').value;
	    				var html='<iframe width=100% height=100% src="'+s+'"/>';
	    				var panels=Ext.ComponentQuery.query('.erpDatalistGridPanel2');
				    	var win=new Ext.window.Window({
				    		height:500,
				    		width:800,
				    		modal:true,
				    		listeners : {
	    	    				close : function(){
	    	    					form.getColumnsAndStore();
	    	    					Ext.Array.each(panels,function(panel){
	    	    						panel.getColumnsAndStore();
	    	    					});
	    	    				}
	    	    			},
				    		html:html});
				    	win.show();
	    			} 
	    }],
	    getColumnsAndStore:function(){
	    	Ext.Ajax.request({
				url : basePath+'common/singleFormItems.action',
				params: {
					caller: this.caller,//formCondition=mt_idIS3489&gridCondition=ct_cuidIS3489
					formCondition:  this.formCondition,
					condition:this.formCondition,
					page: 1,
					pageSize: 5
				},
				method : 'post',
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.exceptionInfo != null){
	        			showError(res.exceptionInfo);return;
	        		}
					data = Ext.decode(res.data);
					var hasData=false;
					if(data){
						hasData=true;
					}
					var items=[];
					var length=0.5;
					if(res.items.length>12){
						length=0.3;
					}
					Ext.each(res.items,function(item){
						var i={};
						i.xtype='displayfield';
						i.columnWidth=length;
						if(item.xtype=='htmleditor'){
							i.columnWidth=1;
						}else if(item.xtype=='hidden'){
							i.xtype='hidden';
						}
						i.fieldLabel=item.fieldLabel;
						i.name=item.name;
						if(hasData){
							i.value=data[item.name];
						}
							items.push(i);
					});
					Ext.getCmp('showform').removeAll();
					Ext.getCmp('showform').add(items);
				}
			});
	    },
		             
		initComponent : function(){
			this.formCondition=/id/g.test(formCondition)?formCondition:formCondition.split('=')[0]+"='"+formCondition.split('=')[1]+"'";
			this.getColumnsAndStore();
			this.callParent(arguments); 
		}
});