Ext.define('erp.view.sysmng.message.MessageSetBar',{
	extend: 'Ext.view.View', 
	alias: 'widget.messagesetbar', 
	border:false,
	style: {
		position: 'absolute'
	},
	itemSelector:'li',
	activeItem:0,
	tpl:[ 
	     '<div id="bbarprocess" class="progressbar" align="center"><ul class="steps">',
	     '<tpl for=".">',
	     '<li class="{type}">',
	     '<span class="font">{desc}</span>',
	     '</li>',
	     '</tpl></ul></div>'],
	     listeners:{
	    	 'itemclick':function(view,record,item,index){
	    	 	
	    	 	 var lis=document.getElementById('bbarprocess').getElementsByTagName('li');	    	 	 
	    	 	 for(var i=0;i<lis.length;i++){
	    	 	 	
	    	 	 	if(i==index){
	    	 	 		lis[i].setAttribute("class","normal active");
	    	 	 		
	    	 	 	}else{
	    	 	 		lis[i].setAttribute("class","normal");
	    	 	 	}
	    	 	 }
	    		 var messagenavpanel=Ext.getCmp('messagenavpanel');
	    		 messagenavpanel.changeCard(messagenavpanel,null,index);				
	    	 }
	     },
	     initComponent : function(){
	    	 var me=this;
		     me.store=Ext.create('Ext.data.Store', {
		    	 fields: [{name: 'itemId'},
		    	          {name:'desc'},{name:'type'}],
		    	          data: me.getData()
		     }),
	    	 this.callParent(arguments);
	     },
	     getData:function(dataview){
	    	 var data=[{ 
	    	 	 desc:'列表',
	    		 type:'active'
	    	},
	    	{ 
	    	 	 desc:'新增',
	    		 type:'normal'
	    	},
	    	];
	 		return data;
	     }
});