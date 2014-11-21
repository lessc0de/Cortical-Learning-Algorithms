package edu.memphis.ccrg.cla.utils;

import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import cern.colt.bitvector.BitVector;

public class MiscUtils {

	private static final int DEFAULT_PRINT_LINE_LENGTH = 50;
	
	public static void print(double[] array, int w, int places) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < array.length; i++){
			if(i%w==0){
				sb.append("\n");
			}
			sb.append(TestUtils.round(array[i],places)+" ");
		}
		System.out.println(sb);
	}

	/**
	 * Prints specified {@link BitVector} using specified line length.
	 * @param bv
	 * @param lineLength
	 */
	public static void print(BitVector bv, int lineLength){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < bv.size(); i++){
			if(i%lineLength==0){
				sb.append("\n");
			}
			if(bv.get(i)){
				sb.append("1 ");
			}else{
				sb.append("_ ");
			}
		}
		System.out.println(sb);
	}

	/**
	 * Prints specified {@link BitVector} using default line length.
	 * @param bv
	 */
	public static void print(BitVector bv){
		print(bv,DEFAULT_PRINT_LINE_LENGTH);
	}

	public static void print(int[][] array) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < array.length; i++){
			sb.append("\n");
			for(int j = 0; j < array[0].length; j++){
				sb.append(array[i][j]+" ");
			}
		}
		System.out.println(sb);
	}

	public static void print(int[] a, int lineLength) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < a.length; i++){
			if(i%lineLength==0){
				sb.append("\n");
			}
			sb.append(a[i] + " ");
		}
		System.out.println(sb);
	}

	/**
	 * Converts specified {@link GlyphVector} to a binary {@link BitVector}.
	 * The rendered BitVector will be of specified render sizes.
	 * @param g a {@link GlyphVector}
	 * @param renderHeight
	 * @param renderWidth
	 * @return {@link BitVector} rendering of g
	 */
	public static BitVector convert(GlyphVector g, int renderHeight, int renderWidth){
		Shape s = g.getOutline();
		Rectangle2D bounds = s.getBounds2D();
		int size = renderHeight*renderWidth;		
		BitVector bv = new BitVector(size);
		for(int i = 0; i < size; i++){
			int h = i / renderWidth;
			int w = i % renderWidth;
			bv.put(i, s.contains(h + bounds.getX(),w + bounds.getY()));
		}
		return bv;
	}
}
