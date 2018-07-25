Ext.ns('Ext.util');

Ext.util.ClipBoard = function(config){

    Ext.util.ClipBoard.superclass.constructor.call(this);
};

Ext.extend(Ext.util.ClipBoard, Ext.util.Observable, {
    board:{
        type:'text',
        content:null,
        path:null,
        source:null,
        paste2Fn:function(obj, type){
            //alert(type);
        }
    },

    getBoard:function(){
        return this.board;
    },

    setBoard:function(board){
        this.board = board;
    }
});
if(false === Ext.type(Ext.util.GlobalBoard)){
	Ext.util.GlobalBoard = new Ext.util.ClipBoard();
}