Ext.define('erp.view.opensys.feedback.feedbackbar',{
	extend: 'Ext.view.View', 
	alias: 'widget.processview', 
	border:false,
	style: {
		position: 'absolute'
	},
	itemSelector:'li',
	activeItem:0,
	/* '<h3><span>{fl_date}<br /> {fl_man}</span>',
	   '<font color="blue">{fl_position:this.formatTitle} &nbsp;&nbsp;</font></h3>',
	   '<p>处理方式 :<font color="green">{fl_kind:this.formatKind}</font> {fl_remark}</p>',*/
	
	     listeners:{
	    	 'itemclick':function(view,record,item,index){
	    		 /* if(item.getAttribute("class") && item.getAttribute("class").indexOf('normal')>-1)
	    			 item.setAttribute("class","normal active");
	    		 else item.setAttribute("class","active");
	    		 var syspanel=Ext.getCmp('syspanel');
	    		 syspanel.changeCard(syspanel,null,index);		*/			
	    	 }
	     },
	     initComponent : function(){
	    	 var me=this,data=me.showdata||me.getData();
	    	 data[0].type='start';
	    	 data[data.length-1].type='end';
	    	 Ext.apply(this,{
	    		tpl:Ext.create('Ext.XTemplate', 
	    			     '<div id="progress" class="progress"><div class="steps">',
	    			     '<tpl for=".">',
	    			     '<p class="{type} remark"><font color="blue">{fl_position} &nbsp;&nbsp;</font> &nbsp;&nbsp;{fl_date}</br>',
	    			     '处理方式 :<font color="green">{fl_kind:this.formatKind}</font> {fl_remark}<p>',
	    			     '<div >',	    			    
	    			     '<tpl if="type != \'start\'">',
	    			     '<span class="circle"></span>',
	    		        '<tpl else>',
	    		          '<span class="active circle"></span>',
	    		        '</tpl>',
	    			     '<tpl if="type != \'end\'">',
	    			     '<span class="lines"></span>',
	    		        '</tpl>',	    			     
	    			     '</div>',
	    			     '</tpl></div></div>',{formatKind: function(value){
							   if(value=='PLAN') return '回复处理';
							   else if(value=='CHANGEHANDLER') return '变更处理人';
							   else if(value=='REVIEW') return '确认处理';}
							   }),
	    	   store: Ext.create('Ext.data.Store', {
					   fields:[{name: 'fl_man' },
					           {name: 'fl_date'},
					           {name: 'fl_position'},
					           {name: 'fl_remark'},
					           {name: 'fl_kind'},
					           {name: 'type'}],
					      data:data
				   })  		     
	    	 });
	    	 this.callParent(arguments);
	     },
	     getData:function(dataview){
	    	 var data=null,id=Ext.getCmp('fb_id').getValue();
	    	 Ext.Ajax.request({
	    		 url : basePath + "common/loadNewGridStore.action",
	    		 params: {
	    			 caller:'Feedback',
	    			 condition:'fl_fbid='+id +' order by fl_date desc'
	    		 },
	    		 async:false,
	    		 method : 'post',
	    		 callback : function(options,success,response){
	    			 var res = new Ext.decode(response.responseText);
	    			 if(res.exceptionInfo){
	    				 showError(res.exceptionInfo);return;
	    			 }
	    			 data = res.data;

	    		 }
	    	 });
	    	 return data;
	     }
});