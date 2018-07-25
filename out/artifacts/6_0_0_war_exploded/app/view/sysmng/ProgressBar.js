Ext.define('erp.view.sysmng.ProgressBar',{
	extend: 'Ext.view.View', 
	alias: 'widget.progressbar', 
	border:false,
	id:'progressbar',
	style: {
		position: 'absolute'
	},
	itemSelector:'li',
	activeItem:0,
	tpl:[ 
	     '<div id="progress" class="progress" align="center"><ul class="steps">',
	     '<tpl for=".">',
	     '<li class="{type}">',	     
	     '<span class="font">{desc}</span>',
	     '<span class="circle"></span>',
	     '</li>',
	     '</tpl></ul></div>'],
	     listeners:{
	    	 'itemclick':function(view,record,item,index){
	    	 	 var lis=document.getElementById('progress').getElementsByTagName('li');
	    	 	 for(var i=0;i<lis.length;i++){
	    	 	 	if(i==index){
	    	 	 		lis[i].setAttribute("class","normal active");
	    	 	 		
	    	 	 	}else{
	    	 	 		lis[i].setAttribute("class","normal");
	    	 	 	}
	    	 	 }
	    		 var mainnavpanel=Ext.getCmp('mainnavpanel');
	    		 mainnavpanel.fireEvent('changeCard', mainnavpanel,null,index);					
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
	    	 	 desc:'基础设置',
	    		 type:'active'
	    	},{
	    		 desc:'功能升级',
	    		 type:'normal'
	 		},
	 		{
	    		 desc:'知会消息',
	    		 type:'Message'
	 		}
	 		];
	 		return data;
	     }
});