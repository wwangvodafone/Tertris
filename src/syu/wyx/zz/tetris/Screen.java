package syu.wyx.zz.tetris;

public class Screen{
    
    public int widthPixels;
    public int heightPixels;
    
    public Screen(){
        
    }
    
    public Screen(int widthPixels,int heightPixels){
        this.widthPixels=widthPixels;
        this.heightPixels=heightPixels;
    }

    @Override
    public String toString() {
        return "("+widthPixels+","+heightPixels+")";
    }
    
}
