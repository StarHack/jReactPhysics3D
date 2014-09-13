package net.smert.jreactphysics3d.mathematics;

import java.util.Objects;

/**
 * This class represents a position and an orientation in 3D. It can also be seen as representing a translation and a
 * rotation.
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class Transform {

    // Position
    private Vector3 mPosition;

    // Orientation
    private Quaternion mOrientation;

    // Constructor
    public Transform() {
        mPosition = new Vector3();
        mOrientation = new Quaternion().identity();
    }

    // Constructor
    public Transform(Vector3 position, Matrix3x3 orientation) {
        mPosition = new Vector3(position);
        mOrientation = new Quaternion(orientation);
    }

    // Constructor
    public Transform(Vector3 position, Quaternion orientation) {
        mPosition = new Vector3(position);
        mOrientation = new Quaternion(orientation);
    }

    // Copy-constructor
    public Transform(Transform transform) {
        mPosition = new Vector3(transform.mPosition);
        mOrientation = new Quaternion(transform.mOrientation);
    }

    // Return the position of the transform
    public Vector3 getPosition() {
        return mPosition;
    }

    // Set the origin of the transform
    public void setPosition(Vector3 position) {
        mPosition = position;
    }

    // Return the rotation matrix
    public Quaternion getOrientation() {
        return mOrientation;
    }

    // Set the rotation matrix of the transform
    public void setOrientation(Quaternion orientation) {
        mOrientation = orientation;
    }

    // Set the transform to the identity transform
    public void setToIdentity() {
        mPosition = new Vector3();
        mOrientation = new Quaternion().identity();
    }

    // Set the transform from an OpenGL transform matrix
    public void setFromOpenGL(float[] openglMatrix) {
        Matrix3x3 matrix = new Matrix3x3(openglMatrix[0], openglMatrix[4], openglMatrix[8],
                openglMatrix[1], openglMatrix[5], openglMatrix[9],
                openglMatrix[2], openglMatrix[6], openglMatrix[10]);
        mOrientation = new Quaternion(matrix);
        mPosition.set(openglMatrix[12], openglMatrix[13], openglMatrix[14]);
    }

    // Get the OpenGL matrix of the transform
    public void getOpenGLMatrix(float[] openglMatrix) {
        Matrix3x3 matrix = new Matrix3x3();
        mOrientation.getMatrix(matrix);
        openglMatrix[0] = matrix.m[0][0];
        openglMatrix[1] = matrix.m[1][0];
        openglMatrix[2] = matrix.m[2][0];
        openglMatrix[3] = 0.0f;
        openglMatrix[4] = matrix.m[0][1];
        openglMatrix[5] = matrix.m[1][1];
        openglMatrix[6] = matrix.m[2][1];
        openglMatrix[7] = 0.0f;
        openglMatrix[8] = matrix.m[0][2];
        openglMatrix[9] = matrix.m[1][2];
        openglMatrix[10] = matrix.m[2][2];
        openglMatrix[11] = 0.0f;
        openglMatrix[12] = mPosition.x;
        openglMatrix[13] = mPosition.y;
        openglMatrix[14] = mPosition.z;
        openglMatrix[15] = 1.0f;
    }

    // Return the inverse of the transform
    public Transform getInverse() {
        Quaternion invQuaternion = new Quaternion(mOrientation).inverse();
        Matrix3x3 invMatrix = new Matrix3x3();
        invQuaternion.getMatrix(invMatrix);
        return new Transform(Matrix3x3.operatorMultiply(invMatrix, new Vector3(mPosition).invert()), invQuaternion);
    }

    // Return an interpolated transform
    public static Transform interpolateTransforms(Transform oldTransform, Transform newTransform, float interpolationFactor) {

        Quaternion interOrientation = new Quaternion();
        Vector3 interPosition = new Vector3(oldTransform.mPosition).multiply(1.0f - interpolationFactor)
                .add(new Vector3(newTransform.mPosition).multiply(interpolationFactor));

        Quaternion.Slerp(oldTransform.mOrientation, newTransform.mOrientation, interpolationFactor, interOrientation);

        return new Transform(interPosition, interOrientation);
    }

    // Return the identity transform
    public static Transform identity() {
        return new Transform(new Vector3(), new Quaternion().identity());
    }

    // Return the transformed vector
    public Vector3 operatorMultiply(Vector3 vector) {
        Matrix3x3 matrix = new Matrix3x3();
        mOrientation.getMatrix(matrix);
        return Matrix3x3.operatorMultiply(matrix, vector).add(mPosition);
    }

    // Operator of multiplication of a transform with another one
    public Transform operatorMultiply(Transform transform2) {
        Matrix3x3 matrix = new Matrix3x3();
        mOrientation.getMatrix(matrix);
        return new Transform(new Vector3(mPosition).add(Matrix3x3.operatorMultiply(matrix, transform2.mPosition)),
                new Quaternion(mOrientation).multiply(transform2.mOrientation));
    }

    // Return true if the two transforms are equal
    public boolean operatorEquals(Transform transform2) {
        return (mPosition == transform2.mPosition) && (mOrientation == transform2.mOrientation);
    }

    // Return true if the two transforms are different
    public boolean operatorNotEquals(Transform transform2) {
        return !(operatorEquals(transform2));
    }

    // Assignment operator
    public Transform operatorEqual(Transform transform) {
        if (transform != this) {
            mPosition = transform.mPosition;
            mOrientation = transform.mOrientation;
        }
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.mPosition);
        hash = 71 * hash + Objects.hashCode(this.mOrientation);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Transform other = (Transform) obj;
        if (!Objects.equals(this.mPosition, other.mPosition)) {
            return false;
        }
        return Objects.equals(this.mOrientation, other.mOrientation);
    }

    @Override
    public String toString() {
        return "(position= " + mPosition + ", orientation= " + mOrientation + ")";
    }

}
