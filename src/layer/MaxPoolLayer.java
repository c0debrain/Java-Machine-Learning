package layer;

import java.nio.ByteBuffer;

import optimizer.Optimizer;
import regularize.Regularizer;
import utils.Activation;
import utils.Tensor;

public class MaxPoolLayer implements Layer{
	private int[] prevSize;
	private int[] nextSize;
	private int winWidth, winHeight;
	private int strideX, strideY;
	private int[][] maxIdx;
	
	public MaxPoolLayer(int winWidth, int winHeight, int strideX, int strideY){
		this.winWidth = winWidth;
		this.winHeight = winHeight;
		this.strideX = strideX;
		this.strideY = strideY;
	}
	
	public MaxPoolLayer(int winSize, int stride){
		this.winWidth = winSize;
		this.winHeight = winSize;
		this.strideX = stride;
		this.strideY = stride;
	}
	
	@Override
	public int[] nextSize(){
		return nextSize;
	}
	
	@Override
	public int[] prevSize(){
		return prevSize;
	}
	
	@Override
	public void init(int[] prevSize){
		this.prevSize = prevSize;
		
		int temp = prevSize[0] - winWidth;
		if(temp % strideX != 0)
			throw new IllegalArgumentException("Bad sizes for max pooling!");
		int w = temp / strideX + 1;
		
		temp = prevSize[1] - winHeight;
		if(temp % strideY != 0)
			throw new IllegalArgumentException("Bad sizes for max pooling!");
		int h = temp / strideY + 1;
		
		nextSize = new int[]{w, h, prevSize[2]};
		maxIdx = new int[nextSize[0] * nextSize[1] * nextSize[2]][2];
	}
	
	@Override
	public Tensor bias(){
		return null;
	}
	
	@Override
	public Tensor weights(){
		return null;
	}
	
	@Override
	public Tensor forwardPropagate(Tensor input, boolean training){
		double[] res = new double[nextSize[0] * nextSize[1] * nextSize[2]];
		int[] shape = input.shape();
		int idx = 0;
		// slide through and computes the max for each location
		// the output should have the same depth as the input
		for(int i = 0; i < nextSize[0] * strideX; i += strideX){
			for(int j = 0; j < nextSize[1] * strideY; j += strideY){
				for(int k = 0; k < shape[2]; k++){ // for each depth slice
					double max = Double.MIN_VALUE;
					
					for(int rx = 0; rx < winWidth; rx++){ // relative x position
						for(int ry = 0; ry < winHeight; ry++){ // relative y position
							// absolute positions
							int x = i + rx;
							int y = j + ry;
							double val = input.flatGet(x * shape[1] * shape[2] + y * shape[2] + k);
							
							if(val > max){
								max = val;
								
								maxIdx[idx][0] = x;
								maxIdx[idx][1] = y;
							}
						}
					}
					
					// max of all values
					res[idx] = max;
					idx++;
				}
			}
		}
		
		return new Tensor(nextSize, res);
	}
	
	@Override
	public Tensor backPropagate(Tensor prevRes, Tensor nextRes, Tensor error, Optimizer optimizer, Regularizer regularizer, int l){
		double[] res = new double[prevSize[0] * prevSize[1] * prevSize[2]];
		int outIdx = 0;
		
		for(int i = 0; i < nextSize[0] * strideX; i += strideX){
			for(int j = 0; j < nextSize[1] * strideY; j += strideY){
				for(int k = 0; k < prevSize[2]; k++){ // for each depth slice
					for(int rx = 0; rx < winWidth; rx++){ // relative x position
						for(int ry = 0; ry < winHeight; ry++){ // relative y position
							// absolute positions
							int x = i + rx;
							int y = j + ry;
							int inIdx = x * prevSize[1] * prevSize[2] + y * prevSize[2] + k;
							
							if(maxIdx[outIdx][0] == x && maxIdx[outIdx][1] == y){
								res[inIdx] += error.flatGet(outIdx);
							}
						}
					}
					
					outIdx++;
				}
			}
		}
		
		return new Tensor(prevSize, res);
	}
	
	@Override
	public void update(){
		// nothing to do
	}
	
	@Override
	public Activation getActivation(){
		return null;
	}
	
	@Override
	public int byteSize(){
		return 0;
	}
	
	@Override
	public ByteBuffer bytes(){
		return null;
	}
	
	@Override
	public void readBytes(ByteBuffer bb){
		// nothing to do
	}
}
